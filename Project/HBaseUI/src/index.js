import React from 'react';
import ReactDOM from 'react-dom';
import {Provider} from 'react-redux'
import {Router, Route, IndexRoute, browserHistory} from 'react-router'
import {syncHistoryWithStore} from 'react-router-redux'
import configureStore from './store/configureStore'
import App from './containers/App';
import KNNQuery from './components/KNNQuery'
import WithinQuery from './components/WithinQuery'
import registerServiceWorker from './registerServiceWorker';

const store = configureStore();
const history = syncHistoryWithStore(browserHistory, store);

ReactDOM.render(
    <Provider store={store}>
        <Router history={history}>
            <Route path="/" component={App}>
                <IndexRoute component={KNNQuery}/>
                <Route path="within" component={WithinQuery}/>
            </Route>
        </Router>
    </Provider>,
    document.getElementById('root')
);
registerServiceWorker();
