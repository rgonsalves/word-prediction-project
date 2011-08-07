// We are in the global Firefox script scope, so only use our own unique global
// names; best to have at most one
// global in the global namespace

var javaConnect = {
	_urls:'',
	_loader:null,
	LiveConnect : {},
	loadJars : function() {
		/*
		 * for(i=0; i<methods.length;i++) methodArray += methods[i];
		 * alert("methods::"+methodArray);
		 */
			Components.utils['import']('resource://prediction/LiveConnectUtils.js', this.LiveConnect);
			var jars = ['URLSetPolicy.jar', 'hsqldb.jar', 
					'prediction.jar'//, 
				//	'cmu_time_awb.jar','cmu_us_kal.jar','cmudict04.jar','cmulex.jar','cmutimelex.jar','en_us.jar','freetts.jar','mbrola.jar'//speech
					];
			alert("jars loading");
			[this._loader, this._urls] = this.LiveConnect.initWithPrivs(java,
					'word-prediction@project.team', jars);
					alert("jars loaded");
//			dbConnection.init();
	},
	init : function(){
		if(this._loader == null){
			this.loadJars();
		}
	}
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
	_db : null,
	loadMain : function(){
		var mClass = javaConnect._loader.loadClass('controller.Main'); 
		this._main = mClass.newInstance();

	},
	connectionString : function(){
		var file = Components.classes["@mozilla.org/file/directory_service;1"]
				.getService(Components.interfaces.nsIProperties).get("ProfD", Components.interfaces.nsIFile);
	
		var path = file.path.replace(/\\/g,"/");
		path += "/extensions/word-prediction@project.team/content/java/hsql/words";
		return "database#file:"+ path;
	},
	boot : function() {
    	this._db = this._main.getDb();
    	if(this._db.getState() == 16)//hsqldb shutdown state
    		this._main.bootDb(this.connectionString());
    	else{
    		alert("hsqldb server state::" + this._db.getState());
    	}
	},
	fill: function(){
		var result = this._main.dbFill();
		alert ("connection result::" + result);
	},
	init : function(){
		if(!this._main){
			javaConnect.init();
			this.loadMain();
			this.boot();
		}
	},
	destroy : function(){
		
	}
};
