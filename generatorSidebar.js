const fs = require('fs')
const querystring = require('querystring')

function SortLikeWin(a, b) {
  var reg = /[0-9]+/g;
  var lista = a.match(reg);
  var listb = b.match(reg);
  if (!lista || !listb) {
      return a.localeCompare(b);
  }
  for (var i = 0, minLen = Math.min(lista.length, listb.length) ; i < minLen; i++) {
      //数字所在位置序号
      var indexa = a.indexOf(lista[i]);
      var indexb = b.indexOf(listb[i]);
      //数字前面的前缀
      var prefixa = a.substring(0, indexa);
      var prefixb = b.substring(0, indexb);
      //数字的string
      var stra = lista[i];
      var strb = listb[i];
      //数字的值
      var numa = parseInt(stra);
      var numb = parseInt(strb);
      //如果数字的序号不等或前缀不等，属于前缀不同的情况，直接比较
      if (indexa != indexb || prefixa != prefixb) {
          return a.localeCompare(b);
      }
      else {
          //数字的string全等
          if (stra === strb) {
              //如果是最后一个数字，比较数字的后缀
              if (i == minLen - 1) {
                  return a.substring(indexa).localeCompare(b.substring(indexb));
              }
              //如果不是最后一个数字，则循环跳转到下一个数字，并去掉前面相同的部分
              else {
                  a = a.substring(indexa + stra.length);
                  b = b.substring(indexa + stra.length);
              }
          }
              //如果数字的string不全等，但值相等
          else if (numa == numb) {
              //直接比较数字前缀0的个数，多的更小
              return strb.lastIndexOf(numb + '') - stra.lastIndexOf(numa + '');
          }
          else {
              //如果数字不等，直接比较数字大小
              return numa - numb;
          }
      }
  }
}

function filterDirectory(name) {
  if (name === '.history' || name === 'images' || name === 'code_file' || name === 'react_demo') {
    return false
  }
  return true
}

function filterFile(name) {
  return !name.startsWith('_') && name.endsWith('.md') && name !== 'README.md'
}

function newLine(deep) {
  return new Array(deep).fill('  ').join('')
}

const rootPath = './soft_dev_doc'
const sidebarFile = rootPath + '/_sidebar.md'
const sidebar = {}

function readDirSync(path) {
  var pa = fs.readdirSync(path);
  let namePaths = path.replace(`${rootPath}`, '').split('/')
  namePaths.shift()
  let that = sidebar
  // point to the right place
  while (namePaths.length) {
    const name = namePaths.shift()
    if (!that[name]) {
      that[name] = {}
    }
    that = that[name]
  }
  pa.forEach(function (ele, index) {
    var info = fs.statSync(path + "/" + ele)
    if (info.isDirectory()) {
      if (filterDirectory(ele)) {
        readDirSync(path + "/" + ele);
      }
    } else if (filterFile(ele)) {
      that[ele] = querystring.escape(`(${path.replace(`${rootPath}/`, '')}/${ele})`)
    }
  })
}

readDirSync(rootPath)
// console.log(sidebar)
// console.log(Object.keys(sidebar.数学.高数基础).sort(SortLikeWin))

// 遍历 sidebar 对象，生成 _sidebar.md 文件
function generateSidebar(sidebar, deep = 0) {
  const keys = Object.keys(sidebar).sort(SortLikeWin)
  for (let key of keys) {
    let sidebarStr = ''
    if (typeof sidebar[key] === 'string') {
      sidebarStr = `${newLine(deep)}- [${key.replace('.md', '')}]${sidebar[key]}\n`
    } else {
      sidebarStr = `${newLine(deep)}- ${key}\n`
    }
    fs.appendFileSync(sidebarFile, sidebarStr, err => {
      if (err) {
        return console.error(err);
      }
    })
    // console.log(sidebarStr)
    if (typeof sidebar[key] === 'object') {
      generateSidebar(sidebar[key], deep + 1)
    }
  }
}

fs.writeFile(sidebarFile, '', function (err) {
  if (err) {
    return console.error(err);
  }
  console.log("Empty written successfully!");
})

generateSidebar(sidebar)


