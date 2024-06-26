const cachedRequest = (request) => {
    let cache = {}
    return async (key) => {
        if (!(key in cache)) {
            cache[key] = request(key)
        }
        return await cache[key]
    }
}

export {
    cachedRequest
}