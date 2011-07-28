
// We are in the global Firefox script scope, so only use our own unique global
// names; best to have at most one
// global in the global namespace

var loader;
var urls;
var objWindow = document.getElementById("msgcomposeWindow");
addEventListener("compose-window-init", function(event) {
			gMsgCompose.RegisterStateListener(myStateListener);
		}, true);

var myStateListener = {
	NotifyComposeFieldsReady : function() {
		java_demo.loadJars();
		init();
		// Add event handler for Thunderbird Message Composer
	},

	NotifyComposeBodyReady : function() {
		if (objWindow != null) {
			objWindow.addEventListener('keyup', function() {
						getWords()
					}, true);
		}
	},

	ComposeProcessDone : function(aResult) {
	},

	SaveInFolderDone : function(folderURI) {
	}
};

var java_demo = {
	LiveConnect : {},
	loadJars : function() {
		/*
		 * for(i=0; i<methods.length;i++) methodArray += methods[i];
		 * alert("methods::"+methodArray);
		 */
		Components.utils['import']('resource://javaDemo/LiveConnectUtils.js',
				this.LiveConnect);
		var jars = ['URLSetPolicy.jar', 'prediction.jar',
				'patricia-trie-0.6.jar', 'mysql-connector-java-5.1.16-bin.jar',
				'commons-logging-api-1.1.1.jar', 'hsqldb.jar',
				'commons-logging-1.1.1.jar', 'RadixTree-0.3.jar'];
		[loader, urls] = this.LiveConnect.initWithPrivs(java,
				'word-prediction@project.team', jars);
		// alert("jars loaded");
	}
};

/*var callGenericWrapper = function() {
	
	// Build an array of the class types which are expected by our constructor
	// (in this case, java.io.File and a class from another JAR we loaded,
	// com.sleepycat.db.EnvironmentConfig)

	var paramtypes = reflect.Array.newInstance(java.lang.Class, 2);
	paramtypes[0] = java.lang.String;
	var envconfigClass = loader.loadClass('model.GenericWrapper');
	paramtypes[1] = envconfigClass;
	// Get the constructor
	var constructor = envconfigClass.getConstructor(paramtypes);
	// Now that we have the constructor with the right parameter types, we can
	// build the specific arguments we wish to pass to it

	var arglist = reflect.Array.newInstance(java.lang.Object, 2); 
	var str = "";
	str += 
	arglist[0] = str;
	var envconfig = envconfigClass.newInstance();
	arglist[1] = envconfig;
	// Call our constructor with our arguments
	var env = constructor.newInstance(arglist);
};*/

var init = function() {
	// var main = java.lang.Class.forName('controller.Main', true, loader);
	// var aStaticMethod = main.getMethod('init', []);
	// var result = aStaticMethod.invoke(null, []);
	// alert(result);
//	callGenericWrapper();
	var file = Components.classes["@mozilla.org/file/directory_service;1"]
			.getService(Components.interfaces.nsIProperties).get("ProfD",
					Components.interfaces.nsIFile);

	var path = file.path.replace(/\\/g,"/") + "/extensions/word-prediction@project.team/content/java/hsql/words";
	var connStr = "jdbc:hsqldb:file:"+path;
	alert("string::"+connStr);
//	path = "\""+file.path+"\"";
//	path.replace(/\\/g,"/");
	var reflect = java.lang.reflect;
	var mClass = loader.loadClass('controller.Main'); // use the same loader
														// from above
	var mInstance = mClass.newInstance();
	var result1 = mInstance.init(connStr);//"jdbc:hsqldb:file:C:\Documents and Settings\Administrator\Application Data\Thunderbird\Profiles\pyp3w7hi.default\extensions\javaDemo@brett.zamir\content\java\hsql\words");//path); 
	alert(result1);
}
var findMatches = function(letter) {
	// alert("finding " + letter +" matches");
	var acClass = loader.loadClass('controller.AutoCompleter'); // use the same
																// loader from
																// above
	var acInstance = acClass.newInstance();
	var array = acInstance.findMatches(letter); // Pass whatever arguments you
												// need (they'll be
												// auto-converted to Java form,
												// taking into account the
												// LiveConnect conversion rules)
	var result = "";
	for (i = 0; i < /* array.length */8; i++)
		result += array[i];
	alert(result);
}

function getWords() {
	var editor = GetCurrentEditor();
	var text = editor.outputToString('text/plain', editor.eNone);
	var letter = text.substring(text.length - 1);
	// alert(letter);
	findMatches(letter);
}
