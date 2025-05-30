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
package io.gravitee.am.gateway.handler.users.service.impl;

import io.gravitee.am.gateway.handler.common.service.RevokeTokenGatewayService;
import io.gravitee.am.gateway.handler.users.service.DomainUserConsentService;
import io.gravitee.am.model.Domain;
import io.gravitee.am.model.UserId;
import io.gravitee.am.model.oauth2.ScopeApproval;
import io.gravitee.am.service.ScopeApprovalService;
import io.gravitee.am.service.exception.ScopeApprovalNotFoundException;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Titouan COMPIEGNE (titouan.compiegne at graviteesource.com)
 * @author GraviteeSource Team
 */
public class DomainUserConsentServiceImpl implements DomainUserConsentService {

    @Autowired
    private Domain domain;

    @Autowired
    private ScopeApprovalService scopeApprovalService;

    @Autowired
    private RevokeTokenGatewayService revokeTokenGatewayService;

    @Override
    public Single<Set<ScopeApproval>> consents(UserId userId) {
        return scopeApprovalService.findByDomainAndUser(domain, userId).collect(HashSet::new, Set::add);
    }

    @Override
    public Single<Set<ScopeApproval>> consents(UserId userId, String clientId) {
        return scopeApprovalService.findByDomainAndUserAndClient(domain, userId, clientId).collect(HashSet::new, Set::add);
    }

    @Override
    public Maybe<ScopeApproval> consent(String consentId) {
        return scopeApprovalService.findById(domain, consentId)
                .switchIfEmpty(Maybe.error(new ScopeApprovalNotFoundException(consentId)));
    }

    @Override
    public Completable revokeConsent(UserId userId, String consentId, io.gravitee.am.identityprovider.api.User principal) {
        return scopeApprovalService.revokeByConsent(domain, userId, consentId, revokeTokenGatewayService::process, principal);
    }

    @Override
    public Completable revokeConsents(UserId userId, io.gravitee.am.identityprovider.api.User principal) {
        return scopeApprovalService.revokeByUser(domain, userId, revokeTokenGatewayService::process, principal);
    }

    @Override
    public Completable revokeConsents(UserId userId, String clientId, io.gravitee.am.identityprovider.api.User principal) {
        return scopeApprovalService.revokeByUserAndClient(domain, userId, clientId, revokeTokenGatewayService::process, principal);
    }

}
