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

package io.gravitee.am.gateway.handler.common.user;


import io.gravitee.am.gateway.handler.common.user.impl.UserStoreImplV2;
import io.gravitee.am.model.User;
import io.gravitee.node.api.cache.Cache;
import io.gravitee.node.api.cache.CacheException;
import io.gravitee.node.api.cache.CacheManager;
import io.reactivex.rxjava3.core.Maybe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Eric LELEU (eric.leleu at graviteesource.com)
 * @author GraviteeSource Team
 */
@ExtendWith(MockitoExtension.class)
public class UserStoreV2Test {

    @Mock
    private Cache<Object, User> cache;

    @Mock
    private Cache<Object, User> gisCache;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Environment environment;

    private UserStoreImplV2 cut;

    @BeforeEach
    public void init() {
        Mockito.when(cacheManager.getOrCreateCache(eq("userStoreById"), any(UserValueMapper.class))).thenReturn(cache);
        Mockito.when(cacheManager.getOrCreateCache(eq("userStoreByGis"), any(UserValueMapper.class))).thenReturn(gisCache);
        Mockito.when(environment.getProperty("user.cache.ttl", Integer.class, 36000)).thenReturn(36000);

        this.cut = new UserStoreImplV2(cacheManager, environment);
    }

    @Test
    public void should_ignore_error_on_get() throws Exception {
        when(cache.rxGet(any())).thenReturn(Maybe.error(new CacheException("error for test")));

        final var observer = cut.get(UUID.randomUUID().toString()).test();
        observer.await(5, TimeUnit.SECONDS);
        observer.assertNoValues();

        verify(cache).rxGet(any());
    }

    @Test
    public void should_ignore_error_on_get_by_gis() throws Exception {
        when(gisCache.rxGet(any())).thenReturn(Maybe.error(new CacheException("error for test")));

        final var observer = cut.getByInternalSub(UUID.randomUUID().toString()).test();
        observer.await(5, TimeUnit.SECONDS);
        observer.assertNoValues();

        verify(gisCache).rxGet(any());
    }

    @Test
    public void should_ignore_error_on_put_idCache() throws Exception {
        when(cache.rxPut(any(), any(), any(Long.class), any())).thenReturn(Maybe.error(new CacheException("error for test")));
        when(gisCache.rxPut(any(), any(), any(Long.class), any())).thenReturn(Maybe.just(new User()));

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        final var observer = cut.add(user).test();
        observer.await(5, TimeUnit.SECONDS);
        observer.assertValueCount(1);

        verify(cache).rxPut(any(), any(), any(Long.class), any());
        verify(gisCache).rxPut(any(), any(), any(Long.class), any());
    }

    @Test
    public void should_ignore_error_on_put_gisCache() throws Exception {
        when(gisCache.rxPut(any(), any(), any(Long.class), any())).thenReturn(Maybe.error(new CacheException("error for test")));
        when(cache.rxPut(any(), any(), any(Long.class), any())).thenReturn(Maybe.just(new User()));

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        final var observer = cut.add(user).test();
        observer.await(5, TimeUnit.SECONDS);
        observer.assertValueCount(1);

        verify(cache).rxPut(any(), any(), any(Long.class), any());
        verify(gisCache).rxPut(any(), any(), any(Long.class), any());
    }

    @Test
    public void should_ignore_error_on_evict_on_idCache() throws Exception {
        when(cache.rxEvict(any())).thenReturn(Maybe.error(new CacheException("error for test")));

        final var observer = cut.remove(UUID.randomUUID().toString()).test();
        observer.await(5, TimeUnit.SECONDS);
        observer.assertNoErrors();

        verify(cache).rxEvict(any());
        verify(gisCache, never()).rxEvict(any());
    }

    @Test
    public void should_ignore_error_on_evict_on_gisCache() throws Exception {
        when(cache.rxEvict(any())).thenReturn(Maybe.just(new User()));
        when(gisCache.rxEvict(any())).thenReturn(Maybe.error(new CacheException("error for test")));

        final var observer = cut.remove(UUID.randomUUID().toString()).test();
        observer.await(5, TimeUnit.SECONDS);
        observer.assertNoErrors();

        verify(cache).rxEvict(any());
        verify(gisCache).rxEvict(any());
    }

}
