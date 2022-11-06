/*
 * Copyright 2022 tandemdude
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

interface Credentials {
    accessToken: null | string,
    refreshToken: null | string,
    expiresAt: null | number,
    store: null | string,
}

export function saveCredentials(creds: Credentials) {
    let store = creds.store == "session" ? sessionStorage : localStorage;
    store.setItem("accessToken", creds.accessToken);
    store.setItem("refreshToken", creds.refreshToken);
    store.setItem("expiresAt", String(creds.expiresAt));
}

export function retrieveCredentials(context: Credentials): Credentials | null {
    let store = null;
    if (localStorage.getItem("accessToken") != null) {
        store = localStorage;
    } else if (sessionStorage.getItem("accessToken") != null) {
        store = sessionStorage;
    }

    if (store != null) {
        context.accessToken = store.getItem("accessToken");
        context.refreshToken = store.getItem("refreshToken");
        context.expiresAt = parseInt(store.getItem("expiresAt"));
        context.store = store == localStorage ? "persistent" : "session";
        return context;
    }
    return null;
}
