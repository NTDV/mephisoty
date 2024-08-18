/**
 * Simple object check.
 * @param item
 * @returns {boolean}
 */
export function isObject(item) {
  return (item && typeof item === 'object' && !Array.isArray(item));
}

/**
 * Deep merge two objects.
 * @param target
 * @param sources
 */
export function mergeDeep(target, ...sources) {
  if (!sources.length) return target;
  const source = sources.shift();

  if (isObject(target) && isObject(source)) {
    for (const key in source) {
      if (isObject(source[key])) {
        if (!target[key]) Object.assign(target, {[key]: {}});
        mergeDeep(target[key], source[key]);
      } else {
        Object.assign(target, {[key]: source[key]});
      }
    }
  }

  return mergeDeep(target, ...sources);
}

export function mapReviver(key, value) {
  if (typeof value === 'object' && value !== null) {
    if (value.dataType === 'Map') {
      return new Map(value.value);
    }
  }
  return value;
}

export function forEachEntry(obj, callback) {
  for (const key in obj)
    if (obj.hasOwnProperty(key))
      callback(obj[key], key);
}

export function getPayload(token) {
  const base64Url = token.split('.')[1];
  const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  return decodeURIComponent(window.atob(base64).split('').map(function(c) {
    return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
  }).join(''));
}

export function getCurrentUser() {
  const payload = localStorage.jwt_payload;
  if (!payload) return null;

  const parsed = JSON.parse(payload);
  if (!parsed) return null;

  return {
    ...parsed.user,
    fullName: `${parsed.user.secondName} ${parsed.user.firstName} ${parsed.user.thirdName}`.trim(),
    fio: `${parsed.user.secondName} ${parsed.user.firstName[0]}.${
      parsed.user.thirdName && parsed.user.thirdName !== '' ? ' ' + parsed.user.thirdName[0] + '.' : ''}`
  };
}