
window.addEventListener("load", function(){onpageload()}, false);

var onpageload = function() {
	alert("onpageload");
	var moduleLoader = Components.classes["@mozilla.org/moz/jssubscript-loader;1"]
			.getService(Components.interfaces.mozIJSSubScriptLoader);
	moduleLoader.loadSubScript("chrome://javaDemo/content/global.js");
	if (javaConnect.loader == null) {
		javaConnect.loadJars();
		dbConnection.fill();
	} else
		alert("jars already there");
	var objWindow = document.getElementById("msgcomposeWindow");
	if (objWindow != null) {
		objWindow.addEventListener('keyup', function() {
					getWords()
				}, true);
	}
}

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

function getWords() {
	var editor = GetCurrentEditor();
	var text = editor.outputToString('text/plain', editor.eNone);
	var letter = text.substring(text.length - 1);
	findMatches(letter);
}

var findMatches = function(letter) {

	var acClass = javaConnect.loader.loadClass('controller.AutoCompleter');
	var acInstance = acClass.newInstance();
	var wordsArry = dbConnection.main.getWordArry();
	var array = acInstance.findMatches(letter, wordsArry); // Pass whatever arguments you
	// need (they'll be
	// auto-converted to Java form,
	// taking into account the
	// LiveConnect conversion rules)
	var result = "";
	for (i = 0; i < /* array.length */8; i++)
		result += array[i];
	alert(result);
}
