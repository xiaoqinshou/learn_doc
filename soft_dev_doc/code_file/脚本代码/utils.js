// 格式化时间
function dateFtt(date) {
  let fmt = 'yyyy-MM-dd hh:mm:ss';
  let o = {
    "M+": date.getMonth() + 1, //月份
    "d+": date.getDate(), //日
    "h+": date.getHours(), //小时
    "m+": date.getMinutes(), //分
    "s+": date.getSeconds(), //秒
    "q+": Math.floor((date.getMonth() + 3) / 3), //季度
    "S": date.getMilliseconds() //毫秒
    };
  if(/(y+)/.test(fmt)){
    fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
  }
  for(let k in o){
    if(new RegExp("(" + k + ")").test(fmt)){
      fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    }
  }
  return fmt;
}

// 驼峰命名转换
function hump(value){
  let index = value.indexOf("_")
  if(index>-1){
    let head = value.substring(0,index)
    let second = value.substring(index+1,index+2)
    let foot = value.substring(index+2)
    return hump(`${head}${second.toUpperCase()}${foot}`)
  }else{
    return value
  }
}

// 单词首字母大写 Word initials(单词首字母缩写)
function wordIntialsToUpperCase(word){
  let letter = word.substr(0,1)
  return word.replace(letter, letter.toUpperCase())
}
