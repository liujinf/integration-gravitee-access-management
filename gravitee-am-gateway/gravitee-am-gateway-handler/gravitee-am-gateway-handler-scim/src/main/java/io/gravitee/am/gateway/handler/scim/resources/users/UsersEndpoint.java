/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.am.gateway.handler.scim.resources.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gravitee.am.common.jwt.JWT;
import io.gravitee.am.common.scim.filter.Filter;
import io.gravitee.am.common.scim.parser.SCIMFilterParser;
import io.gravitee.am.common.utils.ConstantKeys;
import io.gravitee.am.gateway.handler.common.jwt.SubjectManager;
import io.gravitee.am.gateway.handler.common.vertx.core.http.VertxHttpServerRequest;
import io.gravitee.am.gateway.handler.scim.business.CreateUserAction;
import io.gravitee.am.gateway.handler.scim.exception.InvalidSyntaxException;
import io.gravitee.am.gateway.handler.scim.service.ProvisioningUserService;
import io.gravitee.am.identityprovider.api.SimpleAuthenticationContext;
import io.gravitee.am.model.Domain;
import io.gravitee.common.http.HttpHeaders;
import io.gravitee.common.http.MediaType;
import io.reactivex.rxjava3.core.Maybe;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.rxjava3.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Optional;

/**
 * @author Titouan COMPIEGNE (titouan.compiegne at graviteesource.com)
 * @author GraviteeSource Team
 */
@Slf4j
public class UsersEndpoint extends AbstractUserEndpoint {

    private static final int MAX_ITEMS_PER_PAGE = 100;
    private static final int DEFAULT_START_INDEX = 1;

    public UsersEndpoint(Domain domain, ProvisioningUserService userService, ObjectMapper objectMapper, SubjectManager subjectManager) {
        super(domain, userService, objectMapper, subjectManager);
    }

    public void list(RoutingContext context) {
        // Pagination (https://tools.ietf.org/html/rfc7644#section-3.4.2.4)
        Integer startIndex = DEFAULT_START_INDEX;
        Integer size = MAX_ITEMS_PER_PAGE;
        Filter filter = null;

        // The 1-based index of the first query result.
        // A value less than 1 SHALL be interpreted as 1.
        final String startIndexParam = context.request().getParam("startIndex");
        if (StringUtils.isNumeric(startIndexParam)) {
            startIndex = Integer.max(Integer.valueOf(startIndexParam), 1);
        }

        // Non-negative integer. Specifies the desired  results per page, e.g., 10.
        // A negative value SHALL be interpreted as "0".
        // A value of "0"  indicates that no resource results are to be returned except for "totalResults".
        final String count = context.request().getParam("count");
        if (StringUtils.isNumeric(count)) {
            size = Integer.min(Integer.max(Integer.valueOf(count), 0), MAX_ITEMS_PER_PAGE);
        }

        // Filter results
        final String filterParam = context.request().getParam("filter");
        if (filterParam != null && !filterParam.isEmpty()) {
            try {
                filter = SCIMFilterParser.parse(filterParam);
            } catch (Exception ex) {
                context.fail(new InvalidSyntaxException(ex.getMessage()));
                return;
            }
        }

        // user service use 0-based index
        userService.list(filter, startIndex - 1, size, location(context.request()))
                .subscribe(
                        users -> context.response()
                                .putHeader(HttpHeaders.CACHE_CONTROL, "no-store")
                                .putHeader(HttpHeaders.PRAGMA, "no-cache")
                                .putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                .end(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(users)),
                        context::fail);
    }

    /**
     * To router new resources, clients send HTTP POST requests to the resource endpoint, such as "/Users".
     *
     * The server SHALL process attributes according to the following
     *    mutability rules:
     *
     *      In the request body, attributes whose mutability is "readOnly"
     *       (see Sections 2.2 and 7 of [RFC7643]) SHALL be ignored.
     *
     *    o  Attributes whose mutability is "readWrite" (see Section 2.2 of
     *       [RFC7643]) and that are omitted from the request body MAY be
     *       assumed to be not asserted by the client.  The service provider
     *       MAY assign a default value to non-asserted attributes in the final
     *       resource representation.
     *
     *    o  Service providers MAY take into account whether or not a client
     *       has access to all of the resource's attributes when deciding
     *       whether or not non-asserted attributes should be defaulted.
     *
     *    o  Clients that intend to override existing or server-defaulted
     *       values for attributes MAY specify "null" for a single-valued
     *       attribute or an empty array "[]" for a multi-valued attribute to
     *       clear all values.
     *
     * When the service provider successfully creates the new resource, an
     *    HTTP response SHALL be returned with HTTP status code 201 (Created).
     *    The response body SHOULD contain the service provider's
     *    representation of the newly created resource.  The URI of the created
     *    resource SHALL include, in the HTTP "Location" header and the HTTP
     *    body, a JSON representation [RFC7159] with the attribute
     *    "meta.location".
     *
     * If the service provider determines that the creation of the requested
     *    resource conflicts with existing resources (e.g., a "User" resource
     *    with a duplicate "userName"), the service provider MUST return HTTP
     *    status code 409 (Conflict) with a "scimType" error code of
     *    "uniqueness", as per Section 3.12.
     *
     * See <a href="https://tools.ietf.org/html/rfc7644#section-3.3">3.3. Creating Resources</a>
     */
    public void create(RoutingContext context) {
        try {
            final String body = context.body().asString();
            if (body == null) {
                context.fail(new InvalidSyntaxException("Unable to parse body message"));
                return;
            }

            // determine the User resource type via the schemas value
            final Map<String, Object> payload = Json.decodeValue(body, Map.class);
            final String baseUrl = location(context.request());
            SimpleAuthenticationContext authenticationContext = new SimpleAuthenticationContext(new VertxHttpServerRequest(context.request().getDelegate()));
            authenticationContext.attributes().putAll(context.data());

            final JWT accessToken = context.get(ConstantKeys.TOKEN_CONTEXT_KEY);
            principal(accessToken)
                    .map(Optional::ofNullable)
                    .switchIfEmpty(Maybe.just(Optional.empty()))
                    .toSingle()
                    .flatMap(optPrincipal -> new CreateUserAction(userService, getDomain(), context.get(ConstantKeys.CLIENT_CONTEXT_KEY))
                            .execute(baseUrl, payload, authenticationContext, optPrincipal.orElse(null)))
                    .subscribe(
                            user1 -> context.response()
                                    .setStatusCode(201)
                                    .putHeader(HttpHeaders.CACHE_CONTROL, "no-store")
                                    .putHeader(HttpHeaders.PRAGMA, "no-cache")
                                    .putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                    .putHeader(HttpHeaders.LOCATION, user1.getMeta().getLocation())
                                    .end(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(user1)),
                            context::fail);
        } catch (DecodeException ex) {
            context.fail(new InvalidSyntaxException("Unable to parse body message", ex));
        }
    }
}
