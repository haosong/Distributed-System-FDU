import {combineReducers} from 'redux'
import {routerReducer} from 'react-router-redux'
import {REQUEST_POSTS, RECEIVE_POSTS} from '../actions'

function posts(state = {
    isFetching: false,
    didInvalidate: false,
    items: []
}, action) {
    switch (action.type) {
        case REQUEST_POSTS:
            return Object.assign({}, state, {
                isFetching: true,
                didInvalidate: false
            });
        case RECEIVE_POSTS:
            return Object.assign({}, state, {
                isFetching: false,
                didInvalidate: false,
                items: action.data,
            });
        default:
            return state
    }
}

function postsByData(state = {}, action) {
    switch (action.type) {
        case RECEIVE_POSTS:
        case REQUEST_POSTS:
            let url = action.url.replace('/', '_');
            return Object.assign({}, state, {
                [url]: posts(state[action.url], action)
            });
        default:
            return state
    }
}

const rootReducer = combineReducers({
    postsByData,
    routing: routerReducer
});

export default rootReducer;