/*
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
/* Gravitee.io - Access Management API
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

/* tslint:disable */
/* eslint-disable */

import * as runtime from '../runtime';
import {
  NewScope,
  NewScopeFromJSON,
  NewScopeToJSON,
  PatchScope,
  PatchScopeFromJSON,
  PatchScopeToJSON,
  Scope,
  ScopeFromJSON,
  ScopeToJSON,
  ScopePage,
  ScopePageFromJSON,
  ScopePageToJSON,
  UpdateScope,
  UpdateScopeFromJSON,
  UpdateScopeToJSON,
} from '../models';

export interface CreateScopeRequest {
  organizationId: string;
  environmentId: string;
  domain: string;
  newScope: NewScope;
}

export interface DeleteScopeRequest {
  organizationId: string;
  environmentId: string;
  domain: string;
  scope: string;
}

export interface FindScopeRequest {
  organizationId: string;
  environmentId: string;
  domain: string;
  scope: string;
}

export interface ListScopesRequest {
  organizationId: string;
  environmentId: string;
  domain: string;
  page?: number;
  size?: number;
  q?: string;
}

export interface PatchScopeRequest {
  organizationId: string;
  environmentId: string;
  domain: string;
  scope: string;
  patchScope: PatchScope;
}

export interface UpdateScopeRequest {
  organizationId: string;
  environmentId: string;
  domain: string;
  scope: string;
  updateScope: UpdateScope;
}

/**
 *
 */
export class ScopeApi extends runtime.BaseAPI {
  /**
   * User must have the DOMAIN_SCOPE[CREATE] permission on the specified domain or DOMAIN_SCOPE[CREATE] permission on the specified environment or DOMAIN_SCOPE[CREATE] permission on the specified organization
   * Create a scope
   */
  async createScopeRaw(
    requestParameters: CreateScopeRequest,
    initOverrides?: RequestInit | runtime.InitOverideFunction,
  ): Promise<runtime.ApiResponse<Scope>> {
    if (requestParameters.organizationId === null || requestParameters.organizationId === undefined) {
      throw new runtime.RequiredError(
        'organizationId',
        'Required parameter requestParameters.organizationId was null or undefined when calling createScope.',
      );
    }

    if (requestParameters.environmentId === null || requestParameters.environmentId === undefined) {
      throw new runtime.RequiredError(
        'environmentId',
        'Required parameter requestParameters.environmentId was null or undefined when calling createScope.',
      );
    }

    if (requestParameters.domain === null || requestParameters.domain === undefined) {
      throw new runtime.RequiredError(
        'domain',
        'Required parameter requestParameters.domain was null or undefined when calling createScope.',
      );
    }

    if (requestParameters.newScope === null || requestParameters.newScope === undefined) {
      throw new runtime.RequiredError(
        'newScope',
        'Required parameter requestParameters.newScope was null or undefined when calling createScope.',
      );
    }

    const queryParameters: any = {};

    const headerParameters: runtime.HTTPHeaders = {};

    headerParameters['Content-Type'] = 'application/json';

    if (this.configuration && this.configuration.accessToken) {
      const token = this.configuration.accessToken;
      const tokenString = await token('gravitee-auth', []);

      if (tokenString) {
        headerParameters['Authorization'] = `Bearer ${tokenString}`;
      }
    }
    const response = await this.request(
      {
        path: `/organizations/{organizationId}/environments/{environmentId}/domains/{domain}/scopes`
          .replace(`{${'organizationId'}}`, encodeURIComponent(String(requestParameters.organizationId)))
          .replace(`{${'environmentId'}}`, encodeURIComponent(String(requestParameters.environmentId)))
          .replace(`{${'domain'}}`, encodeURIComponent(String(requestParameters.domain))),
        method: 'POST',
        headers: headerParameters,
        query: queryParameters,
        body: NewScopeToJSON(requestParameters.newScope),
      },
      initOverrides,
    );

    return new runtime.JSONApiResponse(response, (jsonValue) => ScopeFromJSON(jsonValue));
  }

  /**
   * User must have the DOMAIN_SCOPE[CREATE] permission on the specified domain or DOMAIN_SCOPE[CREATE] permission on the specified environment or DOMAIN_SCOPE[CREATE] permission on the specified organization
   * Create a scope
   */
  async createScope(requestParameters: CreateScopeRequest, initOverrides?: RequestInit | runtime.InitOverideFunction): Promise<Scope> {
    const response = await this.createScopeRaw(requestParameters, initOverrides);
    return await response.value();
  }

  /**
   * User must have the DOMAIN_SCOPE[DELETE] permission on the specified domain or DOMAIN_SCOPE[DELETE] permission on the specified environment or DOMAIN_SCOPE[DELETE] permission on the specified organization
   * Delete a scope
   */
  async deleteScopeRaw(
    requestParameters: DeleteScopeRequest,
    initOverrides?: RequestInit | runtime.InitOverideFunction,
  ): Promise<runtime.ApiResponse<void>> {
    if (requestParameters.organizationId === null || requestParameters.organizationId === undefined) {
      throw new runtime.RequiredError(
        'organizationId',
        'Required parameter requestParameters.organizationId was null or undefined when calling deleteScope.',
      );
    }

    if (requestParameters.environmentId === null || requestParameters.environmentId === undefined) {
      throw new runtime.RequiredError(
        'environmentId',
        'Required parameter requestParameters.environmentId was null or undefined when calling deleteScope.',
      );
    }

    if (requestParameters.domain === null || requestParameters.domain === undefined) {
      throw new runtime.RequiredError(
        'domain',
        'Required parameter requestParameters.domain was null or undefined when calling deleteScope.',
      );
    }

    if (requestParameters.scope === null || requestParameters.scope === undefined) {
      throw new runtime.RequiredError(
        'scope',
        'Required parameter requestParameters.scope was null or undefined when calling deleteScope.',
      );
    }

    const queryParameters: any = {};

    const headerParameters: runtime.HTTPHeaders = {};

    if (this.configuration && this.configuration.accessToken) {
      const token = this.configuration.accessToken;
      const tokenString = await token('gravitee-auth', []);

      if (tokenString) {
        headerParameters['Authorization'] = `Bearer ${tokenString}`;
      }
    }
    const response = await this.request(
      {
        path: `/organizations/{organizationId}/environments/{environmentId}/domains/{domain}/scopes/{scope}`
          .replace(`{${'organizationId'}}`, encodeURIComponent(String(requestParameters.organizationId)))
          .replace(`{${'environmentId'}}`, encodeURIComponent(String(requestParameters.environmentId)))
          .replace(`{${'domain'}}`, encodeURIComponent(String(requestParameters.domain)))
          .replace(`{${'scope'}}`, encodeURIComponent(String(requestParameters.scope))),
        method: 'DELETE',
        headers: headerParameters,
        query: queryParameters,
      },
      initOverrides,
    );

    return new runtime.VoidApiResponse(response);
  }

  /**
   * User must have the DOMAIN_SCOPE[DELETE] permission on the specified domain or DOMAIN_SCOPE[DELETE] permission on the specified environment or DOMAIN_SCOPE[DELETE] permission on the specified organization
   * Delete a scope
   */
  async deleteScope(requestParameters: DeleteScopeRequest, initOverrides?: RequestInit | runtime.InitOverideFunction): Promise<void> {
    await this.deleteScopeRaw(requestParameters, initOverrides);
  }

  /**
   * User must have the DOMAIN_SCOPE[READ] permission on the specified domain or DOMAIN_SCOPE[READ] permission on the specified environment or DOMAIN_SCOPE[READ] permission on the specified organization
   * Get a scope
   */
  async findScopeRaw(
    requestParameters: FindScopeRequest,
    initOverrides?: RequestInit | runtime.InitOverideFunction,
  ): Promise<runtime.ApiResponse<Scope>> {
    if (requestParameters.organizationId === null || requestParameters.organizationId === undefined) {
      throw new runtime.RequiredError(
        'organizationId',
        'Required parameter requestParameters.organizationId was null or undefined when calling findScope.',
      );
    }

    if (requestParameters.environmentId === null || requestParameters.environmentId === undefined) {
      throw new runtime.RequiredError(
        'environmentId',
        'Required parameter requestParameters.environmentId was null or undefined when calling findScope.',
      );
    }

    if (requestParameters.domain === null || requestParameters.domain === undefined) {
      throw new runtime.RequiredError(
        'domain',
        'Required parameter requestParameters.domain was null or undefined when calling findScope.',
      );
    }

    if (requestParameters.scope === null || requestParameters.scope === undefined) {
      throw new runtime.RequiredError('scope', 'Required parameter requestParameters.scope was null or undefined when calling findScope.');
    }

    const queryParameters: any = {};

    const headerParameters: runtime.HTTPHeaders = {};

    if (this.configuration && this.configuration.accessToken) {
      const token = this.configuration.accessToken;
      const tokenString = await token('gravitee-auth', []);

      if (tokenString) {
        headerParameters['Authorization'] = `Bearer ${tokenString}`;
      }
    }
    const response = await this.request(
      {
        path: `/organizations/{organizationId}/environments/{environmentId}/domains/{domain}/scopes/{scope}`
          .replace(`{${'organizationId'}}`, encodeURIComponent(String(requestParameters.organizationId)))
          .replace(`{${'environmentId'}}`, encodeURIComponent(String(requestParameters.environmentId)))
          .replace(`{${'domain'}}`, encodeURIComponent(String(requestParameters.domain)))
          .replace(`{${'scope'}}`, encodeURIComponent(String(requestParameters.scope))),
        method: 'GET',
        headers: headerParameters,
        query: queryParameters,
      },
      initOverrides,
    );

    return new runtime.JSONApiResponse(response, (jsonValue) => ScopeFromJSON(jsonValue));
  }

  /**
   * User must have the DOMAIN_SCOPE[READ] permission on the specified domain or DOMAIN_SCOPE[READ] permission on the specified environment or DOMAIN_SCOPE[READ] permission on the specified organization
   * Get a scope
   */
  async findScope(requestParameters: FindScopeRequest, initOverrides?: RequestInit | runtime.InitOverideFunction): Promise<Scope> {
    const response = await this.findScopeRaw(requestParameters, initOverrides);
    return await response.value();
  }

  /**
   * User must have the DOMAIN_SCOPE[LIST] permission on the specified domain or DOMAIN_SCOPE[LIST] permission on the specified environment or DOMAIN_SCOPE[LIST] permission on the specified organization Each returned scope is filtered and contains only basic information such as id, key, name, description, isSystem and isDiscovery.
   * List scopes for a security domain
   */
  async listScopesRaw(
    requestParameters: ListScopesRequest,
    initOverrides?: RequestInit | runtime.InitOverideFunction,
  ): Promise<runtime.ApiResponse<ScopePage>> {
    if (requestParameters.organizationId === null || requestParameters.organizationId === undefined) {
      throw new runtime.RequiredError(
        'organizationId',
        'Required parameter requestParameters.organizationId was null or undefined when calling listScopes.',
      );
    }

    if (requestParameters.environmentId === null || requestParameters.environmentId === undefined) {
      throw new runtime.RequiredError(
        'environmentId',
        'Required parameter requestParameters.environmentId was null or undefined when calling listScopes.',
      );
    }

    if (requestParameters.domain === null || requestParameters.domain === undefined) {
      throw new runtime.RequiredError(
        'domain',
        'Required parameter requestParameters.domain was null or undefined when calling listScopes.',
      );
    }

    const queryParameters: any = {};

    if (requestParameters.page !== undefined) {
      queryParameters['page'] = requestParameters.page;
    }

    if (requestParameters.size !== undefined) {
      queryParameters['size'] = requestParameters.size;
    }

    if (requestParameters.q !== undefined) {
      queryParameters['q'] = requestParameters.q;
    }

    const headerParameters: runtime.HTTPHeaders = {};

    if (this.configuration && this.configuration.accessToken) {
      const token = this.configuration.accessToken;
      const tokenString = await token('gravitee-auth', []);

      if (tokenString) {
        headerParameters['Authorization'] = `Bearer ${tokenString}`;
      }
    }
    const response = await this.request(
      {
        path: `/organizations/{organizationId}/environments/{environmentId}/domains/{domain}/scopes`
          .replace(`{${'organizationId'}}`, encodeURIComponent(String(requestParameters.organizationId)))
          .replace(`{${'environmentId'}}`, encodeURIComponent(String(requestParameters.environmentId)))
          .replace(`{${'domain'}}`, encodeURIComponent(String(requestParameters.domain))),
        method: 'GET',
        headers: headerParameters,
        query: queryParameters,
      },
      initOverrides,
    );

    return new runtime.JSONApiResponse(response, (jsonValue) => ScopePageFromJSON(jsonValue));
  }

  /**
   * User must have the DOMAIN_SCOPE[LIST] permission on the specified domain or DOMAIN_SCOPE[LIST] permission on the specified environment or DOMAIN_SCOPE[LIST] permission on the specified organization Each returned scope is filtered and contains only basic information such as id, key, name, description, isSystem and isDiscovery.
   * List scopes for a security domain
   */
  async listScopes(requestParameters: ListScopesRequest, initOverrides?: RequestInit | runtime.InitOverideFunction): Promise<ScopePage> {
    const response = await this.listScopesRaw(requestParameters, initOverrides);
    return await response.value();
  }

  /**
   * User must have the DOMAIN_SCOPE[UPDATE] permission on the specified domain or DOMAIN_SCOPE[UPDATE] permission on the specified environment or DOMAIN_SCOPE[UPDATE] permission on the specified organization
   * Patch a scope
   */
  async patchScopeRaw(
    requestParameters: PatchScopeRequest,
    initOverrides?: RequestInit | runtime.InitOverideFunction,
  ): Promise<runtime.ApiResponse<Scope>> {
    if (requestParameters.organizationId === null || requestParameters.organizationId === undefined) {
      throw new runtime.RequiredError(
        'organizationId',
        'Required parameter requestParameters.organizationId was null or undefined when calling patchScope.',
      );
    }

    if (requestParameters.environmentId === null || requestParameters.environmentId === undefined) {
      throw new runtime.RequiredError(
        'environmentId',
        'Required parameter requestParameters.environmentId was null or undefined when calling patchScope.',
      );
    }

    if (requestParameters.domain === null || requestParameters.domain === undefined) {
      throw new runtime.RequiredError(
        'domain',
        'Required parameter requestParameters.domain was null or undefined when calling patchScope.',
      );
    }

    if (requestParameters.scope === null || requestParameters.scope === undefined) {
      throw new runtime.RequiredError('scope', 'Required parameter requestParameters.scope was null or undefined when calling patchScope.');
    }

    if (requestParameters.patchScope === null || requestParameters.patchScope === undefined) {
      throw new runtime.RequiredError(
        'patchScope',
        'Required parameter requestParameters.patchScope was null or undefined when calling patchScope.',
      );
    }

    const queryParameters: any = {};

    const headerParameters: runtime.HTTPHeaders = {};

    headerParameters['Content-Type'] = 'application/json';

    if (this.configuration && this.configuration.accessToken) {
      const token = this.configuration.accessToken;
      const tokenString = await token('gravitee-auth', []);

      if (tokenString) {
        headerParameters['Authorization'] = `Bearer ${tokenString}`;
      }
    }
    const response = await this.request(
      {
        path: `/organizations/{organizationId}/environments/{environmentId}/domains/{domain}/scopes/{scope}`
          .replace(`{${'organizationId'}}`, encodeURIComponent(String(requestParameters.organizationId)))
          .replace(`{${'environmentId'}}`, encodeURIComponent(String(requestParameters.environmentId)))
          .replace(`{${'domain'}}`, encodeURIComponent(String(requestParameters.domain)))
          .replace(`{${'scope'}}`, encodeURIComponent(String(requestParameters.scope))),
        method: 'PATCH',
        headers: headerParameters,
        query: queryParameters,
        body: PatchScopeToJSON(requestParameters.patchScope),
      },
      initOverrides,
    );

    return new runtime.JSONApiResponse(response, (jsonValue) => ScopeFromJSON(jsonValue));
  }

  /**
   * User must have the DOMAIN_SCOPE[UPDATE] permission on the specified domain or DOMAIN_SCOPE[UPDATE] permission on the specified environment or DOMAIN_SCOPE[UPDATE] permission on the specified organization
   * Patch a scope
   */
  async patchScope(requestParameters: PatchScopeRequest, initOverrides?: RequestInit | runtime.InitOverideFunction): Promise<Scope> {
    const response = await this.patchScopeRaw(requestParameters, initOverrides);
    return await response.value();
  }

  /**
   * User must have the DOMAIN_SCOPE[UPDATE] permission on the specified domain or DOMAIN_SCOPE[UPDATE] permission on the specified environment or DOMAIN_SCOPE[UPDATE] permission on the specified organization
   * Update a scope
   */
  async updateScopeRaw(
    requestParameters: UpdateScopeRequest,
    initOverrides?: RequestInit | runtime.InitOverideFunction,
  ): Promise<runtime.ApiResponse<Scope>> {
    if (requestParameters.organizationId === null || requestParameters.organizationId === undefined) {
      throw new runtime.RequiredError(
        'organizationId',
        'Required parameter requestParameters.organizationId was null or undefined when calling updateScope.',
      );
    }

    if (requestParameters.environmentId === null || requestParameters.environmentId === undefined) {
      throw new runtime.RequiredError(
        'environmentId',
        'Required parameter requestParameters.environmentId was null or undefined when calling updateScope.',
      );
    }

    if (requestParameters.domain === null || requestParameters.domain === undefined) {
      throw new runtime.RequiredError(
        'domain',
        'Required parameter requestParameters.domain was null or undefined when calling updateScope.',
      );
    }

    if (requestParameters.scope === null || requestParameters.scope === undefined) {
      throw new runtime.RequiredError(
        'scope',
        'Required parameter requestParameters.scope was null or undefined when calling updateScope.',
      );
    }

    if (requestParameters.updateScope === null || requestParameters.updateScope === undefined) {
      throw new runtime.RequiredError(
        'updateScope',
        'Required parameter requestParameters.updateScope was null or undefined when calling updateScope.',
      );
    }

    const queryParameters: any = {};

    const headerParameters: runtime.HTTPHeaders = {};

    headerParameters['Content-Type'] = 'application/json';

    if (this.configuration && this.configuration.accessToken) {
      const token = this.configuration.accessToken;
      const tokenString = await token('gravitee-auth', []);

      if (tokenString) {
        headerParameters['Authorization'] = `Bearer ${tokenString}`;
      }
    }
    const response = await this.request(
      {
        path: `/organizations/{organizationId}/environments/{environmentId}/domains/{domain}/scopes/{scope}`
          .replace(`{${'organizationId'}}`, encodeURIComponent(String(requestParameters.organizationId)))
          .replace(`{${'environmentId'}}`, encodeURIComponent(String(requestParameters.environmentId)))
          .replace(`{${'domain'}}`, encodeURIComponent(String(requestParameters.domain)))
          .replace(`{${'scope'}}`, encodeURIComponent(String(requestParameters.scope))),
        method: 'PUT',
        headers: headerParameters,
        query: queryParameters,
        body: UpdateScopeToJSON(requestParameters.updateScope),
      },
      initOverrides,
    );

    return new runtime.JSONApiResponse(response, (jsonValue) => ScopeFromJSON(jsonValue));
  }

  /**
   * User must have the DOMAIN_SCOPE[UPDATE] permission on the specified domain or DOMAIN_SCOPE[UPDATE] permission on the specified environment or DOMAIN_SCOPE[UPDATE] permission on the specified organization
   * Update a scope
   */
  async updateScope(requestParameters: UpdateScopeRequest, initOverrides?: RequestInit | runtime.InitOverideFunction): Promise<Scope> {
    const response = await this.updateScopeRaw(requestParameters, initOverrides);
    return await response.value();
  }
}
