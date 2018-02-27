/*
 * Author: sveera
 */
fdescribe('My First Jasmine test case', () => {

    beforeAll(() => {
        console.log('Before All Test cases');
    });

    afterAll(() => {
        console.log('After All Test cases');
    });

    beforeEach(() => {
        console.log('Before Each Test case');
    });

    afterEach(() => {
        console.log('After Each Test case');
    });

    fit('Test Multiplication', () => {
        expect(3 * 4).toBe(12);
    });
    fit('Test Addition', () => {
        expect(3 + 4).toBe(7);
    });

});
