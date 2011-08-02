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
			var jars = ['URLSetPolicy.jar', 'prediction.jar', 'hsqldb.jar',
					'cmulex.jar', 'en_us.jar',  'freetts.jar','RadixTree-0.3.jar'];
			[this.loader, this.urls] = this.LiveConnect.initWithPrivs(java,
					'word-prediction@project.team', jars);
//			dbConnection.init();
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

//Constructor with parameters
//var mainClass = javaConnect.loader.loadClass('controller.Main');
//var theClass = java.lang.Class.forName("java.lang.Class");
//var paramtypes = java.lang.reflect.Array.newInstance(theClass, 1);
//var stringClass = java.lang.Class.forName('java.lang.String');
//paramtypes[0] = stringClass;
//var mConstructor = mainClass.getConstructor(paramtypes);
//var theObj = java.lang.Class.forName("java.lang.Object");
//var arglist = java.lang.reflect.Array.newInstance(theObj, 1);
//arglist[0] = connStr;
//this._main = mConstructor.newInstance(arglist);
var dbConnection = {
	_main : null,
	loadMain : function(){
		javaConnect.loadJars();
		var mClass = javaConnect.loader.loadClass('controller.Main'); 
		this._main = mClass.newInstance();
//		var result = this._main.init(connStr);//"jdbc:hsqldb:file:C:/Documents and Settings/Administrator/Application Data/Thunderbird/Profiles/pyp3w7hi.default/extensions/word-prediction@project.team/content/java/hsql/words");//path); 
		alert("mainLoad");
	    
	},
	boot : function() {
		
		var file = Components.classes["@mozilla.org/file/directory_service;1"]
				.getService(Components.interfaces.nsIProperties).get("ProfD", Components.interfaces.nsIFile);
	
		var path = file.path.replace(/\\/g,"/");
		path += "/extensions/word-prediction@project.team/content/java/hsql/words";
		var connStr = "file:"+ path;
    	this.loadMain();
    	alert("boot");
    	this._main.bootDb(connStr);
	},
	fill: function(){
		alert("fill");
		if(!this._main){
			this.loadMain();
		}
//		window.setTimeout(new function(){this._main.bootDb(connStr);},5000);
		var result = this._main.dbFill();//"jdbc:hsqldb:file:C:/Documents and Settings/Administrator/Application Data/Thunderbird/Profiles/pyp3w7hi.default/extensions/word-prediction@project.team/content/java/hsql/words");//path); 
		alert("connection result::" + result);
	},
	destroy : function(){
		
	}
};
