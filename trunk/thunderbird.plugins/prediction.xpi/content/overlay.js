
window.addEventListener("load", function(){Autocompleter.init();TextToVoice.init();}, false);
window.addEventListener("unload", function(){Autocompleter.destroy()}, false);
var moduleLoader = Components.classes["@mozilla.org/moz/jssubscript-loader;1"]
		.getService(Components.interfaces.mozIJSSubScriptLoader);
moduleLoader.loadSubScript("chrome://javaDemo/content/global.js");
var WORD_SEPARATORS = "[\\s,'.';]";
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
//	_wordsArry : null,
	_objWindow : document.getElementById("msgcomposeWindow"),
	_cursorPrevPos : 0,
	_cursorPostPos : 0,
	MAXIMUM_PREDICTIONS : 0,
	init : function(){
		if (!this._acInstance) {
			alert("globalVars");
			
			dbConnection.fill();
			acClass = javaConnect.loader.loadClass('controller.AutoCompleter');
			this._acInstance = acClass.newInstance();
			MAXIMUM_PREDICTIONS = this._acInstance.getMAXIMUM_PREDICTIONS();
//			this._wordsArry = dbConnection._main.getWordArry();
		} else
			alert("globalVars already there");
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
			ttvClass = javaConnect.loader.loadClass('utilities.TextToVoice');
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
		if (character == ' ' && Autocompleter._text.charAt(Autocompleter._text.length - 1) == '\n')
			Autocompleter._text = Autocompleter._text.substring(0, Autocompleter._text.length - 2) + " " + Autocompleter._text.substring(Autocompleter._text.length - 2);
		else if(character == SPEECH_WHEN_CHAR)//'.'
			TextToVoice._ttvInstance.speech(Autocompleter._text);
			
	}

	if(!(""+character).match(patterns._wordSeparatorPattern))
		findMatches(Autocompleter._text);
	
}

var findMatches = function(text) {
	var prefix =findPrefix(text);
	if(prefix.length > 0){
		var array = Autocompleter._acInstance.findMatches(prefix.toLowerCase());
		var result = "";
		for (i = 0; i < MAXIMUM_PREDICTIONS; i++)
			if(array[i])
				result += array[i];
		alert(result);
		

	}
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
    	var array = text.substring(0, Autocompleter._cursorPostPos).split(patterns._wordSeparatorPattern);
    	var len = array.length - 1;
    	var prefix = "";
    	for(i = len; i >= 0; i--){
    		if(array[i])
    			return array[i];
    	}
    }
