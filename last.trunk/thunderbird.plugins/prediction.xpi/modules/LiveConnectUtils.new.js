var EXPORTED_SYMBOLS = [
    // LiveConnect JAR loading and necessary privilege escalation
    // 'init', // Could cause other extensions to interfere since loader is static
    'initWithPrivs',
    'policyAdd',
    // General utilities for LiveConnect
    'javaObject'
];

var Cc = Components.classes;
var Ci = Components.interfaces;
var JAVA;



// For more background on the code used here, see http://forums.java.net/jive/thread.jspa?threadID=45933&tstart=0

/**
 * Gives a Java URLClassLoader and array of java.net.URL's with privileges already assigned and
 *    used to call Java methods within the JARs referenced by the java.net.URL's array
 * This may be the only method necessary
 * @param {String} ext_id The extension's ID; we could hard-code this instead, but making the function more
 *                                              flexible in case code module ever standardized or made into dependency
 * @param {String[]} jarFiles The JAR directories to import, relative to the root of the extension, or if the file has
 *                                                      base_dir as type "undefined", "content/java/" will be assumed
 * @param {String} [base_dir] The base directory for resolution of the JAR files; defaults to "content/java"
 * @param {java} [java] The java object (probably ok to rely on default)
 * @returns {Array} Array with first element as java.net.URLClassLoader, and second as array of java.net.URL's
 */
function initWithPrivs (ext_id, jarFiles, base_dir, java) {
    var [loader, urls] = init(ext_id, jarFiles, base_dir, java);
    policyAdd(loader, urls);
    return [loader, urls];
}

/**
 * Gives a Java URLClassLoader and array of java.net.URL's which can optionally be assigned privileges and
 *    used to call Java methods within the JARs referenced by the java.net.URL's array
 * NOTE: Remember to include the classLoader as one of the JARs!
 * @param {String} ext_id The extension's ID; we could hard-code this instead, but making the function more
 *                                              flexible in case code module ever standardized or made into dependency
 * @param {String[]} jarFiles The JAR directories to import, relative to the root of the extension, or if the file has
 *                                                      base_dir as type "undefined", "content/java/" will be assumed
 * @param {String} [base_dir] The base directory for resolution of the JAR files; defaults to "content/java"
 * @param {java} [java] The java object (probably ok to rely on default)
 * @returns {Array} Array with first element as java.net.URLClassLoader, and second as array of java.net.URL's
 */
function init (ext_id, jarFiles, base_dir, java) {
    // SETUP
    var wm = Cc['@mozilla.org/appshell/window-mediator;1'].getService(Ci.nsIWindowMediator);
    var mainWin = wm.getMostRecentWindow('navigator:browser');
    JAVA = java || mainWin.java;
    var em = Cc['@mozilla.org/extensions/manager;1'].getService(Ci.nsIExtensionManager);
    var extDir = em.getInstallLocation(ext_id);
    var fURLs = [];

    base_dir = (base_dir == null) ? 'content/java/' : base_dir;

    // ADD
    for (var i = 0; i < jarFiles.length; i++) {
        var dirPos = jarFiles[i].lastIndexOf('/');
        var dir = base_dir + jarFiles[i].substring(0, dirPos+1); // latter part will be empty string if dirPos is -1
        var file = jarFiles[i].slice(dirPos+1);
        var jarDir = extDir.getItemFile(ext_id, dir);
        if (jarDir.exists()) {
            var jarFile = extDir.getItemFile(ext_id, dir+file);
            if (jarFile.exists()) {
                var fileStr = _getUrlSpec(jarFile);
                fURLs[i] = new JAVA.net.URL(fileStr);
            }
            else {
                throw new Error('JAR File not found: '+jarDir.path);
            }
        }
        else {
            throw new Error('JAR Directory not found: '+jarDir.path);
        }
    }

    // fURLs = _toUrlArray(fURLs); // Don't need this as of Java 6 update 12
    var classLoader = new JAVA.net.URLClassLoader(fURLs);
    return [classLoader, fURLs]; // Add policy later (if need to do)
}

/**
 * Critical utility to give file access (or other) permissions to one's Java JAR files
 * @author Adapted from http://simile.mit.edu/wiki/Java_Firefox_Extension (BSD)
 * @param {java.net.URLClassLoader} loader The Firefox class loader which will load the Java class URLSetPolicy
 *                                                                                      (fix: can we do the class loading here entirely and avoid
 *                                                                                                adding class loader to JARs until here?)
 * @param {Array} urls An array of (java.net.URL) URLS to JAR files for which one wishes to add permissions
 */
function policyAdd (loader, urls) {
    try {
        //var str = new JAVA.lang.String('edu.mit.simile.firefoxClassLoader.URLSetPolicy'); // Don't need this as of Java 6 update 12 (auto-string conversions working again)
        var str = 'edu.mit.simile.javaFirefoxExtensionUtils.URLSetPolicy';
        var policyClass = JAVA.lang.Class.forName(
            str,
            true,
            loader
        );
        var policy = policyClass.newInstance();
        policy.setOuterPolicy(JAVA.security.Policy.getPolicy());
        JAVA.security.Policy.setPolicy(policy);
        policy.addPermission(new JAVA.security.AllPermission());
        for (var j=0; j < urls.length; j++) {
            policy.addURL(urls[j]);
        }
    } catch(e) {
        throw new Error(e+'::'+e.lineNumber);
    }
}

// PUBLIC UTILITIES
/**
 * This is a very convenient, if small, utility for creating LiveConnect Java class objects;
 * potentially useful in any LiveConnect; could be even more useful if loader stored
 * @param {java.net.URLClassLoader} loader The class loader
 * @param {String} clss The class to instantiate
 * @returns An instance of the user-specified Java class
 */
function javaObject (loader, clss) {
    var classWrapper = loader.loadClass(clss);
    return classWrapper.newInstance();
}

// PRIVATE UTILITIES

/**
 * Utility for getting file URL from a nsIFile object
 * @param {Components.interfaces.nsIFile} file File for which to obtain its file URL
 * @returns The file URL as a string
 */
function _getUrlSpec (file) {
    // Returns nsIFile for the given extension's file
    var ios = Cc['@mozilla.org/network/io-service;1'].getService(Ci.nsIIOService);
    var url = ios.newFileURI(file);
    return url.spec;
}

// DON'T NEED ANYMORE (needed before with buggy Firefox implementation of LiveConnect before Java6v12)
/**
 * Utility to workaround a Firefox bug in handling Java arrays; converts to a Java-friendly array
 * @a {Array} JavaScript Array to be converted to Java format
 * @returns A Java-friendly array of java.net.URL's
 */
function _toUrlArray (a) {
    // from http://simile.mit.edu/repository/java-firefox-extension/firefox/chrome/content/scripts/browser-overlay.js
      var urlArray = JAVA.lang.reflect.Array.newInstance(JAVA.lang.Class.forName('java.net.URL'), a.length);
      for (var i = 0; i < a.length; i++) {
          var url = a[i];
          urlArray[i] = (typeof url == 'string') ? new JAVA.net.URL(url) : url;
      }
      return urlArray;
}
