import fetch from 'isomorphic-fetch'

export const REQUEST_POSTS = 'REQUEST_POSTS';
export const RECEIVE_POSTS = 'RECEIVE_POSTS';

export function fetchPosts(url, param = "") {
    return dispatch => {
        dispatch(requestPosts(url));
        // return fetch(`http://localhost:3004/${url}${param}`)
        return fetch(`http://10.221.169.40:8080/${url}${param}`)
            .then(response => response.json())
            .then(json => dispatch(receivePosts(url, json)))
    }
}

export function fetchPostsWithData(url, body) {
    return dispatch => {
        dispatch(requestPosts(url));
        // return fetch(`http://localhost:3004/${url}`, {
        return fetch(`http://10.221.169.40:8080/${url}`, {
            method: 'POST',
            // headers: {
            //     'Content-Type': 'application/json'
            // },
            body: body
        })
            .then(response => response.json())
            .then(json => dispatch(receivePosts(url, json)))
    }
}

export function requestPosts(url) {
    return {
        type: REQUEST_POSTS,
        url
    }
}

export function receivePosts(url, json) {
    return {
        type: RECEIVE_POSTS,
        url,
        data: json,
    }
}