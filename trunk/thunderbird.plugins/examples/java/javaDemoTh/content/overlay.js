
// We are in the global Firefox script scope, so only use our own unique global names; best to have at most one
// global in the global namespace
var java_demo = {
    LiveConnect : {},
    onMenuItemCommand : function () {
        Components.utils['import']('resource://javaDemo/LiveConnectUtils.js', this.LiveConnect);
        var jars = ['URLSetPolicy.jar', 'Test.jar'];
        var [loader,urls] = this.LiveConnect.initWithPrivs(java, 'javaDemo@brett.zamir', jars);

        var aClass = java.lang.Class.forName('Test', true, loader);
        var aStaticMethod = aClass.getMethod('getGreetings', []); // Fails here
        var greeting = aStaticMethod.invoke(null, []);
        alert(greeting);
    }
};