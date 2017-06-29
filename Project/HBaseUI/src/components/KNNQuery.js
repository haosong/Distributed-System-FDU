import React, {Component} from 'react';
import PropTypes from 'prop-types'
import {Breadcrumb, Button, Popover, Row, Col, Slider, Spin} from 'antd';
import {Map, Marker, Popup, TileLayer} from 'react-leaflet';
import L from 'leaflet'

const position = [40.76098703, -73.97000655];

const WifiList = ({markers}) => {
    const items = markers.map(({key, position, children}) => (
        <Marker position={position} key={key} icon={L.icon({
            iconUrl: "./wifi.png",
            iconSize: [45, 45],
            popupAnchor: [0, -18]
        })}>
            <Popup>
                <span>{children}</span>
            </Popup>
        </Marker>
    ));
    return <div style={{display: 'none'}}>{items}</div>
};

const SelectList = ({markers}) => {
    const items = markers.map(({key, position, children}) => (
        <Marker position={position} key={key}>
            <Popup>
                <span>{children}</span>
            </Popup>
        </Marker>
    ));
    return <div style={{display: 'none'}}>{items}</div>
};

SelectList.propTypes = {
    markers: PropTypes.array.isRequired,
};
WifiList.propTypes = {
    markers: PropTypes.array.isRequired,
};

class KNNQuery extends Component {
    static contextTypes = {
        store: PropTypes.object.isRequired,
    };

    constructor(props) {
        super(props);
        this.state = {
            latlng: [],
            knn: [],
            searched: false,
            selectKNNVisible: false,
            KValue: 5,
            loading: false
        };
    }

    handleClick = (e) => {
        let point = [{
            key: 'point0',
            position: [e.latlng.lat, e.latlng.lng],
            children: 'Selection Point'
        }];
        this.setState({latlng: point});
        // this.refs.map.leafletElement.locate()
    };

    handleSearch = () => {
        this.setState({loading: true});
        const {fetchPostsWithData, fetchPosts} = this.props;
        this.setState({selectKNNVisible: false});
        let postData = {
            k: this.state.KValue,
            lat: this.state.latlng[0].position[0],
            lng: this.state.latlng[0].position[1]
        };
        console.log(postData);
        postData = JSON.stringify(postData);
        // fetchPosts(`KNNQuery`).then(() => {
            fetchPostsWithData(`KNNQuery`, postData).then(() => {
            let res = this.context.store.getState().postsByData.KNNQuery.items;
            if (res) {
                console.log(res);
            }
            this.setState({loading: false});
            let markers = [];
            let index = 0;
            for (let m of res) {
                markers.push({
                    key: 'marker' + index++,
                    position: [m.lat, m.lng],
                    children: 'Wifi Name: '  + m.name + " | Addr: " + m.address
                })
            }
            this.setState({knn: markers});
            this.setState({searched: true});
        });
    };

    handleReset = () => {
        this.setState({latlng: []});
        this.setState({searched: false});
    };

    handleVisibleChange = (visible) => {
        this.setState({selectKNNVisible: visible})
    };

    onKValueChange = (value) => {
        this.setState({KValue: value})
    };

    render() {
        const content = (
            <div>
                <Row>
                    <Col span={20}>
                        <Slider min={1} max={20} onChange={this.onKValueChange} value={this.state.KValue}/>
                    </Col>
                    <Col span={4}>
                        <Button className="sliderBtn" type="primary" icon="check" size="small" shape="circle"
                                onChange={this.onChange} onClick={this.handleSearch}/>
                    </Col>
                </Row>
            </div>
        );
        return (
            <div>
                <Spin className="mySpin" spinning={this.state.loading} size="large" tip="Finding Wi-Fi ...">
                    <Breadcrumb style={{margin: '12px 0'}}>
                        <Breadcrumb.Item>Menu</Breadcrumb.Item>
                        <Breadcrumb.Item>KNN Query</Breadcrumb.Item>
                    </Breadcrumb>
                    <div className="controlPanel">
                        { this.state.latlng.length > 0 ?
                            <Popover placement="rightTop" title="Select K" content={content} trigger="click"
                                     visible={this.state.selectKNNVisible} onVisibleChange={this.handleVisibleChange}>
                                <Button className="controlBtn" type="primary" shape="circle" icon="search"
                                        size="large"/>
                            </Popover>
                            : null}
                        { this.state.searched ?
                            <Button className="controlBtn" type="danger" shape="circle" icon="reload"
                                    size="large"
                                    onClick={this.handleReset}/>
                            : null }
                    </div>
                    <div style={{padding: 24, background: '#fff', minHeight: 360}} className="myMap">
                        <Map animate={true} center={position} zoom={13} onClick={this.handleClick}
                             ref="map">
                            <TileLayer
                                url='http://{s}.tile.osm.org/{z}/{x}/{y}.png'
                                attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
                            />
                            <SelectList markers={this.state.latlng}/>
                            { this.state.searched ? [
                                <WifiList markers={this.state.knn} key="1"/>
                            ] : null }
                        </Map>
                    </div>
                </Spin>
            </div>
        );
    }
}

export default KNNQuery;
