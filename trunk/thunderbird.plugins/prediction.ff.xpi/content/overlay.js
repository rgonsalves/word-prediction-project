
// We are in the global Firefox script scope, so only use our own unique global names; best to have at most one
// global in the global namespace
var java_demo = {
    LiveConnect : {},
    onMenuItemCommand : function () {
//        var jars = ['URLSetPolicy.jar', 'Test.jar'];
//        var [loader,urls] = this.LiveConnect.initWithPrivs(java, 'javaDemo@brett.zamir', jars);
//
//        var aClass = java.lang.Class.forName('Test', true, loader);
//        var aStaticMethod = aClass.getMethod('getGreetings', []); // Fails here
//        var greeting = aStaticMethod.invoke(null, []);
        Components.utils['import']('resource://javaDemo/LiveConnectUtils.js', this.LiveConnect);
        var jars = ['URLSetPolicy.jar','prediction.jar','patricia-trie-0.6.jar', 'mysql-connector-java-5.1.16-bin.jar', 
        			'commons-logging-api-1.1.1.jar', 'hsqldb.jar', 'commons-logging-1.1.1.jar', 'RadixTree-0.3.jar'];
//       var  jars = [ _jars];
       var  [loader,urls] = this.LiveConnect.initWithPrivs(java, 'javaDemo@brett.zamir', jars);
        alert("url::"+urls);
       var  main = java.lang.Class.forName('controller.Main', true, loader);
       var methods = main.getMethods();
       var methodArray = "";
      /* for(i=0; i<methods.length;i++)
       	  methodArray += methods[i];
       alert("methods::"+methodArray);*/
       
	   var aStaticMethod = main.getMethod('init', []); // Fails here
//        alert("aStaticMethod::"+aStaticMethod);
       var  result = aStaticMethod.invoke(null, []);
//        alert(result);
    }
};