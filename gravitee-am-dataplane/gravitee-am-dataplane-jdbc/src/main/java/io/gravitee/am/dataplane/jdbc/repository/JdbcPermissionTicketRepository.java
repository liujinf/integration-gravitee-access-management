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
package io.gravitee.am.dataplane.jdbc.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import io.gravitee.am.common.utils.RandomString;
import io.gravitee.am.dataplane.api.repository.PermissionTicketRepository;
import io.gravitee.am.dataplane.jdbc.repository.model.uma.JdbcPermissionTicket;
import io.gravitee.am.dataplane.jdbc.repository.spring.uma.SpringPermissionTicketRepository;
import io.gravitee.am.model.uma.PermissionRequest;
import io.gravitee.am.model.uma.PermissionTicket;
import io.gravitee.am.repository.jdbc.provider.common.DateHelper;
import io.gravitee.am.repository.jdbc.provider.common.JSONMapper;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.ZoneOffset.UTC;
import static org.springframework.data.relational.core.query.Criteria.where;
import static reactor.adapter.rxjava.RxJava3Adapter.monoToCompletable;
import static reactor.adapter.rxjava.RxJava3Adapter.monoToSingle;

/**
 * @author Eric LELEU (eric.leleu at graviteesource.com)
 * @author GraviteeSource Team
 */
@Repository
public class JdbcPermissionTicketRepository extends AbstractJdbcRepository implements PermissionTicketRepository, InitializingBean {

    public static final String COL_ID = "id";
    public static final String COL_CLIENT_ID = "client_id";
    public static final String COL_DOMAIN = "domain";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_CREATED_AT = "created_at";
    public static final String COL_EXPIRE_AT = "expire_at";
    public static final String COL_PERMISSION_REQUEST = "permission_request";

    private static final List<String> columns = List.of(
            COL_ID,
            COL_CLIENT_ID,
            COL_DOMAIN,
            COL_USER_ID,
            COL_CREATED_AT,
            COL_EXPIRE_AT,
            COL_PERMISSION_REQUEST
    );

    private String insertStatement;
    private String updateStatement;

    @Autowired
    protected SpringPermissionTicketRepository permissionTicketRepository;

    protected PermissionTicket toEntity(JdbcPermissionTicket entity) {
        final var result = new PermissionTicket();
        result.setPermissionRequest(JSONMapper.toCollectionOfBean(entity.getPermissionRequest(), new TypeReference<List<PermissionRequest>>(){}));
        result.setClientId(entity.getClientId());
        result.setUserId(entity.getUserId());
        result.setDomain(entity.getDomain());
        result.setId(entity.getId());
        result.setExpireAt(DateHelper.toDate(entity.getExpireAt()));
        result.setCreatedAt(DateHelper.toDate(entity.getCreatedAt()));
        return result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.insertStatement = createInsertStatement("uma_permission_ticket", columns);
        this.updateStatement = createUpdateStatement("uma_permission_ticket", columns, List.of(COL_ID));
    }

    @Override
    public Maybe<PermissionTicket> findById(String id) {
        LOGGER.debug("findById({})", id);
        LocalDateTime now = LocalDateTime.now(UTC);
        return permissionTicketRepository.findById(id)
                .filter(bean -> bean.getExpireAt() == null || bean.getExpireAt().isAfter(now))
                .map(this::toEntity);
    }

    @Override
    public Single<PermissionTicket> create(PermissionTicket item) {
        item.setId(item.getId() == null ? RandomString.generate() : item.getId());
        LOGGER.debug("create PermissionTicket with id {}", item.getId());

        DatabaseClient.GenericExecuteSpec insertSpec = getTemplate().getDatabaseClient().sql(insertStatement);

        insertSpec = addQuotedField(insertSpec, COL_ID, item.getId(), String.class);
        insertSpec = addQuotedField(insertSpec, COL_CLIENT_ID, item.getClientId(), String.class);
        insertSpec = addQuotedField(insertSpec, COL_DOMAIN, item.getDomain(), String.class);
        insertSpec = addQuotedField(insertSpec, COL_USER_ID, item.getUserId(), String.class);
        insertSpec = addQuotedField(insertSpec, COL_CREATED_AT, dateConverter.convertTo(item.getCreatedAt(), null), LocalDateTime.class);
        insertSpec = addQuotedField(insertSpec, COL_EXPIRE_AT, dateConverter.convertTo(item.getExpireAt(), null), LocalDateTime.class);
        insertSpec = databaseDialectHelper.addJsonField(insertSpec, COL_PERMISSION_REQUEST, item.getPermissionRequest());

        Mono<Long> action = insertSpec.fetch().rowsUpdated();

        return monoToSingle(action).flatMap((i) -> permissionTicketRepository.findById(item.getId()).map(this::toEntity).toSingle());
    }

    @Override
    public Single<PermissionTicket> update(PermissionTicket item) {
        LOGGER.debug("update PermissionTicket with id {}", item.getId());


        DatabaseClient.GenericExecuteSpec update = getTemplate().getDatabaseClient().sql(updateStatement);

        update = addQuotedField(update, COL_ID, item.getId(), String.class);
        update = addQuotedField(update, COL_CLIENT_ID, item.getClientId(), String.class);
        update = addQuotedField(update, COL_DOMAIN, item.getDomain(), String.class);
        update = addQuotedField(update, COL_USER_ID, item.getUserId(), String.class);
        update = addQuotedField(update, COL_CREATED_AT, dateConverter.convertTo(item.getCreatedAt(), null), LocalDateTime.class);
        update = addQuotedField(update, COL_EXPIRE_AT, dateConverter.convertTo(item.getExpireAt(), null), LocalDateTime.class);
        update = databaseDialectHelper.addJsonField(update, COL_PERMISSION_REQUEST, item.getPermissionRequest());

        Mono<Long> action = update.fetch().rowsUpdated();
        return monoToSingle(action).flatMap((i) -> this.findById(item.getId()).toSingle());
    }

    @Override
    public Completable delete(String id) {
        LOGGER.debug("delete({})", id);
        return permissionTicketRepository.deleteById(id);
    }

    @Override
    public Completable purgeExpiredData() {
        LOGGER.debug("purgeExpiredData()");
        LocalDateTime now = LocalDateTime.now(UTC);
        return monoToCompletable(getTemplate().delete(JdbcPermissionTicket.class).matching(Query.query(where(COL_EXPIRE_AT).lessThan(now))).all());
    }
}
