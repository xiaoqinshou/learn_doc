// 通过 CommonJS 规范导入 show 函数
import testES6 from "./utils/testES6";
import testTs from "./utils/testTs";
import * as React from "react";
import { render } from "react-dom";

const estest = new testES6();
const testts = new testTs();
import show = require("./utils/show.js");
class Main extends React.Component{
    render() {
        // show('Webpack');
        estest.test();
        // testES6.test();
        console.log(testES6);
        console.log(estest);
        testts.test();
        // testTs.test();
        console.log(testts);
        console.log(testTs);
        return (<h1>Hello,Webpack</h1>);
    }
}

render(<Main/>, window.document.getElementById('app'));

