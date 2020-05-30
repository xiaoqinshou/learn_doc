const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
    // 开发模式
    mode: 'development',
    // JavaScript 执行入口文件
    entry: './src/main.tsx',
    output: {
        // 把所有依赖的模块合并输出到一个 bundle.js 文件
        filename: 'bundle.js',
        // 输出文件都放到 dist 目录下
        path: path.resolve(__dirname, './dist'),
    },
    resolve: {
        // 先尝试 tsx ts 后缀的 TypeScript 源码文件
        extensions: ['.tsx', '.ts', '.js']
    },
    module: {
        rules: [
            {
                test: /\.(tsx|ts)$/,
                loader: 'awesome-typescript-loader'
            },
            {
                test: /\.js$/,
                use: ['babel-loader'],
            },
        ]
    },
    plugins: [
        new HtmlWebpackPlugin({
            template: path.join(__dirname, 'index.html'),
            hash: true,
        })
    ],
    devServer: {
        contentBase: path.join(__dirname, 'dist'),
        inline: true
    },
    // 输出 source-map 方便直接调试 ES6 源码
    devtool: 'source-map',

};