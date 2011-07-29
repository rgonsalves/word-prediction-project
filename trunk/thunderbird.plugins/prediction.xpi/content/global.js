// We are in the global Firefox script scope, so only use our own unique global
// names; best to have at most one
// global in the global namespace

var javaConnect = {
	urls:'',
	loader:null,
	LiveConnect : {},
	loadJars : function() {
		/*
		 * for(i=0; i<methods.length;i++) methodArray += methods[i];
		 * alert("methods::"+methodArray);
		 */
			Components.utils['import']('resource://javaDemo/LiveConnectUtils.js', this.LiveConnect);
			var jars = ['URLSetPolicy.jar', 'prediction.jar',
					'patricia-trie-0.6.jar', 
					'commons-logging-api-1.1.1.jar', 'hsqldb.jar',
					'commons-logging-1.1.1.jar', 'RadixTree-0.3.jar'];
			[this.loader, this.urls] = this.LiveConnect.initWithPrivs(java,
					'word-prediction@project.team', jars);
			alert("jars loaded");
		
	}/*,
	init : function(){
		if(loader == null){
			this.loadJars();
		}
	}*/
};
//one
// var main = java.lang.Class.forName('controller.Main', true, loader);
	// var aStaticMethod = main.getMethod('init', []);
	// var result = aStaticMethod.invoke(null, []);
	// alert(result);
//other
//	var mClass = javaConnect.loader.loadClass('controller.Main'); 
//	var mInstance = mClass.newInstance();
//	var result1 = mInstance.init(connStr);//"jdbc:hsqldb:file:C:/Documents and Settings/Administrator/Application Data/Thunderbird/Profiles/pyp3w7hi.default/extensions/word-prediction@project.team/content/java/hsql/words");//path); 
//	alert("connection result::" + result1);
var dbConnection = {
	main : null,
	fill : function() {
		var file = Components.classes["@mozilla.org/file/directory_service;1"]
				.getService(Components.interfaces.nsIProperties).get("ProfD", Components.interfaces.nsIFile);
	
		var path = file.path.replace(/\\/g,"/");
		path += "/extensions/word-prediction@project.team/content/java/hsql/words";
		var connStr = "jdbc:hsqldb:file:"+path;
	
	//	var reflect = java.lang.reflect;
	//    var paramtypes = reflect.Array.newInstance(java.lang.Class, 1); 
		//this even if it seems similar does not work
	    var theClass = java.lang.Class.forName("java.lang.Class");
		var paramtypes = java.lang.reflect.Array.newInstance(theClass, 1);
	    var stringClass = java.lang.Class.forName('java.lang.String');
	
	    paramtypes[0] = stringClass;
	    var mainClass = javaConnect.loader.loadClass('controller.Main');
	
	    var mConstructor = mainClass.getConstructor(paramtypes);
	    var theObj = java.lang.Class.forName("java.lang.Object");
	    var arglist = java.lang.reflect.Array.newInstance(theObj, 1);
	    arglist[0] = connStr;
	    
	    this.main = mConstructor.newInstance(arglist);
		
		this.main.init();
		var result = this.main.init();//"jdbc:hsqldb:file:C:/Documents and Settings/Administrator/Application Data/Thunderbird/Profiles/pyp3w7hi.default/extensions/word-prediction@project.team/content/java/hsql/words");//path); 
		alert("connection result::" + result);
	}
};
