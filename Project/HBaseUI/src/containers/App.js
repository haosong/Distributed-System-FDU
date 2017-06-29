import React, {Component} from 'react';
import {bindActionCreators} from 'redux'
import {connect} from 'react-redux'
import {Layout, Menu, Icon} from 'antd';
import {Link} from 'react-router'
import PropTypes from 'prop-types'
import * as Actions from '../actions'
import '../style/App.css';

const {Content, Sider} = Layout;

class App extends Component {
    static contextTypes = {
        store: PropTypes.object.isRequired,
    };

    constructor(props) {
        super(props);
        this.state = {
            collapsed: false,
            mode: 'inline',
            selectedKey: '1'
        };
    }

    componentWillMount() {
        if (this.props.routes[this.props.routes.length - 1].path === "within") {
            this.setState({selectedKey: '2'})
        }
    }

    onCollapse = (collapsed) => {
        console.log(collapsed);
        this.setState({
            collapsed,
            mode: collapsed ? 'vertical' : 'inline',
        });
    };

    render() {
        const {fetchPosts, fetchPostsWithData} = this.props;
        return (
            <Layout>
                <Sider
                    collapsible
                    collapsed={this.state.collapsed}
                    onCollapse={this.onCollapse}
                >
                    <div className="logo"/>
                    <Menu theme="dark" mode={this.state.mode} defaultSelectedKeys={[this.state.selectedKey]}>
                        <Menu.Item key="1">
                            <Link to="/">
                                <Icon type="dot-chart"/><span className="nav-text">KNN Query</span>
                            </Link>
                        </Menu.Item>
                        <Menu.Item key="2">
                            <Link to="/within">
                                <Icon type="bar-chart"/><span className="nav-text">Within Query</span>
                            </Link>
                        </Menu.Item>
                    </Menu>
                </Sider>
                <Layout>
                    <Content style={{margin: '0 16px'}}>
                        {React.cloneElement(this.props.children, {
                            fetchPosts: fetchPosts,
                            fetchPostsWithData: fetchPostsWithData
                        })}
                        {/*{this.props.children}*/}
                    </Content>
                </Layout>
            </Layout>
        );
    }
}

const mapStateToProps = state => {
    return state
};

const mapDispatchToProps = dispatch => {
    return bindActionCreators(Actions, dispatch);
};

export default connect(mapStateToProps, mapDispatchToProps)(App);
