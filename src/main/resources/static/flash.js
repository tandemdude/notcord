function flash(message) {
    let cookieVal = $.cookie("flashes");
    if (cookieVal == null || cookieVal.trim().length === 0) {
        cookieVal = JSON.stringify([message])
    } else {
        cookieVal = JSON.stringify(JSON.parse(cookieVal).push(message));
    }
    $.cookie("flashes", cookieVal);
}


function getFlashes() {
    let cookieVal = $.cookie("flashes");
    if (cookieVal == null || cookieVal.trim().length === 0) {
        return [];
    }
    return JSON.parse(cookieVal);
}


function clearFlashes() {
    $.cookie("flashes", null);
}

function removeFlash(message) {
    let cookieVal = $.cookie("flashes");
    cookieVal = (cookieVal == null || cookieVal.trim().length === 0) ? [] : JSON.parse(cookieVal);
    cookieVal.remove(message);
    $.cookie("flashes", cookieVal.length === 0 ? null : JSON.stringify(cookieVal));
}
