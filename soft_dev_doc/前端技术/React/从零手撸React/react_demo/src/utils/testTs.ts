class testTs {
    static decs = {
        hello: 'hello',
        world: 'world',
    };

    public test: any = (): void =>{
        console.log(testTs.decs.hello+testTs.decs.world);
    }
}

export default testTs;