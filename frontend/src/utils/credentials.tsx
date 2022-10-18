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
