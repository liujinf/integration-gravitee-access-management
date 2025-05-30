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
package io.gravitee.am.service;

import io.gravitee.am.model.AccountAccessToken;
import io.gravitee.am.model.Reference;
import io.gravitee.am.model.ReferenceType;
import io.gravitee.am.model.User;
import io.gravitee.am.model.UserId;
import io.gravitee.am.model.common.Page;
import io.gravitee.am.repository.management.api.search.FilterCriteria;
import io.gravitee.am.service.model.NewAccountAccessToken;
import io.gravitee.am.service.model.NewUser;
import io.gravitee.am.service.model.UpdateUser;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author Titouan COMPIEGNE (titouan.compiegne at graviteesource.com)
 * @author GraviteeSource Team
 */
public interface OrganizationUserService {


    Flowable<User> findByIdIn(List<String> ids);

    Single<Page<User>> findAll(ReferenceType referenceType, String referenceId, int page, int size);

    Single<Page<User>> search(ReferenceType referenceType, String referenceId, String query, int page, int size);

    Single<Page<User>> search(ReferenceType referenceType, String referenceId, FilterCriteria filterCriteria, int page, int size);
    Flowable<User> search(ReferenceType referenceType, String referenceId, FilterCriteria filterCriteria);

    @Deprecated
    default Maybe<User> findByUsernameAndSource(ReferenceType referenceType, String referenceId, String username, String source) {
        return findByUsernameAndSource(new Reference(referenceType, referenceId), username, source);
    }

    Maybe<User> findByUsernameAndSource(Reference reference, String username, String source);

    /** @deprecated prefer findById(Reference, UserId) for new code
     */
    Single<User> findById(ReferenceType referenceType, String referenceId, String id);

    Single<User> findById(Reference reference, UserId userId);

    Maybe<User> findByExternalIdAndSource(ReferenceType referenceType, String referenceId, String externalId, String source);

    Single<User> create(ReferenceType referenceType, String referenceId, NewUser newUser);

    Single<User> create(User user);

    Single<User> update(ReferenceType referenceType, String referenceId, String id, UpdateUser updateUser);

    Single<User> update(User user);

    /**
     * Set the ORGANIZATION_USER role to a newly create user.
     * @param principal of the user (may be null if creation comes from the Console action, not from a login)
     * @param user on who the default role must be applied
     */
    Completable setRoles(io.gravitee.am.identityprovider.api.User principal, io.gravitee.am.model.User user);

    /**
     * See {@link #setRoles(io.gravitee.am.identityprovider.api.User, User)} with null principal
     * @param user
     * @return
     */
    Completable setRoles(io.gravitee.am.model.User user);

    Flowable<AccountAccessToken> findUserAccessTokens(String organisationId, String userId);

    Single<AccountAccessToken> generateAccountAccessToken(User user, NewAccountAccessToken newAccountToken, String issuer);

    Completable revokeUserAccessTokens(ReferenceType referenceType, String organizationId, String userId);

    Single<User> findByAccessToken(String token, String tokenValue);

    Maybe<AccountAccessToken> revokeToken(String organizationId, String userId, String tokenId);

    Single<User> delete(String userId);
}
