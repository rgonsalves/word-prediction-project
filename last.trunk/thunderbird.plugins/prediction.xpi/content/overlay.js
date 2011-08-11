
var LiveConnect = {};
var moduleLoader = Components.classes["@mozilla.org/moz/jssubscript-loader;1"]
		.getService(Components.interfaces.mozIJSSubScriptLoader);
//moduleLoader.loadSubScript("chrome://prediction/content/global.js");
Components.utils['import']('resource://prediction/LiveConnectUtils.js', LiveConnect);
window.addEventListener("load", function(){/*javaConnect.init();*/dbConnection.init();Autocompleter.init();/*TextToVoice.init();*/}, false);
window.addEventListener("unload", function(){Autocompleter.destroy()}, false);
var WORD_SEPARATORS = "[\\s,.;]";
var WORD_ENDS = "[\\W]";
var LINE_END = "[$]";
var SPEECH_WHEN_CHAR = String.fromCharCode(190); //'.'
var patterns ={
	_wordEndPattern : new RegExp(WORD_ENDS),
    _wordSeparatorPattern : new RegExp(WORD_SEPARATORS)
};

var Autocompleter = {
	_acInstance : null,
	_text : '',
	_objWindow : document.getElementById("msgcomposeWindow"),
	_cursorPrevPos : 0,
	_cursorPostPos : 0,
	MAXIMUM_PREDICTIONS : 0,
	setStatusText : function(text){document.getElementById("statusText").label = text;}, 
	init : function(){
		if (!this._acInstance) {
			dbConnection.fill();
			acClass = LiveConnect.loader.loadClass('controller.AutoCompleter');
			this._acInstance = acClass.newInstance();
			MAXIMUM_PREDICTIONS = this._acInstance.getMAXIMUM_PREDICTIONS();
		}
		if (this._objWindow != null) {
			this._objWindow.addEventListener('keyup', getWords, true);
		}
		if (this._objWindow != null) {
			this._objWindow.addEventListener('keydown', previous, true);
		}
	},
	destroy : function(){
		dbConnection.destroy();
	}
}

var TextToVoice = {
	_ttvInstance : null,
	init : function(){
		if(!this._ttvInstance){
			ttvClass = LiveConnect.loader.loadClass('utilities.TextToVoice');
			this._ttvInstance = ttvClass.newInstance();
		}
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
function previous(){
	selection = content.getSelection();
	var start = selection.getRangeAt(0);
	Autocompleter._cursorPrevPos = start.startOffset;
}

function getWords(e) {
	selection = content.getSelection();
	var start = selection.getRangeAt(0);
	Autocompleter._cursorPostPos = start.startOffset;
	var editor = GetCurrentEditor();
	Autocompleter._text = editor.outputToString('text/plain', editor.eNone);
	
	if(e != null){
		if (e.keyCode) code = e.keyCode;
		else if (e.which) code = e.which;
		
		character = String.fromCharCode(code);
		if ((character).match(patterns._wordSeparatorPattern) || character == SPEECH_WHEN_CHAR){
			/*if (character == ' ' && Autocompleter._text.charAt(Autocompleter._text.length - 1) == '\n')
				Autocompleter._text = Autocompleter._text.substring(0, Autocompleter._text.length - 2) + " " + Autocompleter._text.substring(Autocompleter._text.length - 2);
			else*/
				if(character == SPEECH_WHEN_CHAR)//'.'
					try{
						TextToVoice._ttvInstance.speech(Autocompleter._text);
					}
					catch(e){
						alert(e);
					}
			Autocompleter.setStatusText("");
		}
		else if (!(character).match(patterns._wordSeparatorPattern))
				findMatches(Autocompleter._text);
			
	}
}

var findMatches = function(text) {
	var prefix =findPrefix(text);
	if(prefix){
		var array = Autocompleter._acInstance.findMatches(prefix);
		var result = "";
		for (i = 0; i < MAXIMUM_PREDICTIONS; i++)
			if(array[i])
				result += array[i];

		Autocompleter.setStatusText(result);
	}
	else Autocompleter.setStatusText("");
}

    /***
     * This one will find the word where the cursor is, begining from the nearest separator character. For that the position of the cursor is get, last
     * @param text all the input text
     * @return the last word
     */
    findWord = function(text){
    	var __text = text;
    	var step =0;
    	step = text.match(patterns._wordSeparatorPattern) && text.length > 2?2:0;
    	var array = text.split(patterns._wordSeparatorPattern);
    	return array[array.length - 1 - step];
    }
    findPrefix = function(text){
    	var __text = text;
    	var step =0;
    	var line = text.substring(0, Autocompleter._cursorPostPos);
    	var array = line.split(patterns._wordSeparatorPattern);
    	var len = array.length - 1;
    	var prefix = "";
    	for(i = len; i >= 0; i--){
    		if(array[i]){
    			prefix = array[i];
    			break;
    		}
    	}
    	var lineNoBreaks = line.replace(/(\r\n|\n|\r)/gm,"");
    	if(lineNoBreaks.charAt(lineNoBreaks.length -1) == prefix.charAt(prefix.length - 1))
    		return prefix;
    }
    
    var dbConnection = {
	_main : null,
	_db : null,
	loadMain : function(){
		var mClass = LiveConnect.loader.loadClass('controller.Main'); 
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
//			javaConnect.init();
			this.loadMain();
			this.boot();
		}
	},
	destroy : function(){
		
	}
};