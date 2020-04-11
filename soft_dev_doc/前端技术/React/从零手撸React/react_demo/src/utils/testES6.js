class testES6 {

    constructor(){
        this.state={
            a:'hello',
            b:'world',
        };
    }

    test() {
      console.log(this.state.a+this.state.b);
    }
}
export default  testES6;