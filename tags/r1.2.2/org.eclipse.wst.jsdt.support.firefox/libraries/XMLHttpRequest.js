/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************
 *
 * Based on information from https://developer.mozilla.org/En/XMLHttpRequest and http://msdn2.microsoft.com/en-us/library/ms534308.aspx
 **/

/**
* Object XMLHttpRequest
* @super Global
* @type constructor
* @memberOf Global
* @since FireFox 2.0
*/
XMLHttpRequest.prototype=new Global();
function XMLHttpRequest(){};
/**
 * property channel
 * @type nsIChannel
 * @memberOf XMLHttpRequest
 * @since FireFox 2.0
 */
XMLHttpRequest.prototype.channel=new Object();
/**
 * property mozBackgroundRequest
 * @type Boolean
 * @memberOf XMLHttpRequest
 * @since FireFox 2.0
 */
XMLHttpRequest.prototype.mozBackgroundRequest=false;
/**
 * property multipart
 * @type Boolean
 * @memberOf XMLHttpRequest
 * @since FireFox 2.0
 */
XMLHttpRequest.prototype.multipart=false;
/**
 * function onreadystatechange
 * @memberOf XMLHttpRequest
 * @since FireFox 2.0
 */
XMLHttpRequest.prototype.onreadystatechange=function(){};
/**
 * property readyState
 * @type Number
 * @memberOf XMLHttpRequest
 * @since FireFox 2.0
 */
XMLHttpRequest.prototype.readyState=1;
/**
 * property responseText
 * @type String
 * @memberOf XMLHttpRequest
 * @since FireFox 2.0
 */
XMLHttpRequest.prototype.responseText="";
/**
 * property responseXML
 * @type Document
 * @memberOf XMLHttpRequest
 * @since FireFox 2.0
 */
XMLHttpRequest.prototype.responseXML=new Document();
/**
 * property status
 * @type Number
 * @memberOf XMLHttpRequest
 * @since FireFox 2.0
 */
XMLHttpRequest.prototype.status=0;
/**
 * property statusText
 * @type String
 * @memberOf XMLHttpRequest
 * @since FireFox 2.0
 */
XMLHttpRequest.prototype.statusText="";
/**
 * property upload
 * @type nsIXMLHttpRequestUpload
 * @memberOf XMLHttpRequest
 * @since FireFox 2.0
 */
XMLHttpRequest.prototype.upload=new Object();
/**
 * property withCredentials
 * @type Boolean
 * @memberOf XMLHttpRequest
 * @since FireFox 2.0
 */
XMLHttpRequest.prototype.withCredentials=false;

/**
 * function abort()
 * @memberOf XMLHttpRequest
 * @since FireFox 2.0
 */
XMLHttpRequest.prototype.abort=function(){};
/**
* function getAllResponseHeaders()
* @type String
* @memberOf XMLHttpRequest
* @since FireFox 2.0
*/
XMLHttpRequest.prototype.getAllResponseHeaders=function(){return "";};

///**
// * function open(method, url)
// * @param {String} method
// * @param {String} url
// * @memberOf XMLHttpRequest
// * @since FireFox 2.0
// */
//XMLHttpRequest.prototype.open=function(method, url){};

///**
// * function open(method, url, async)
// * @param {String} method
// * @param {String} url
// * @param {Boolean} async
// * @memberOf XMLHttpRequest
// * @since FireFox 2.0
// */
//XMLHttpRequest.prototype.open=function(method, url, async){};

/**
* function open(method, url, async, username, password)
* @param {String} method
* @param {String} url
* @param {Boolean} optional async
* @param {String} optional username
* @param {String} optional password
* @memberOf XMLHttpRequest
* @since FireFox 2.0
*/
XMLHttpRequest.prototype.open=function(method, url, async, username, password){};

/**
* function overrideMimeType(mimetype)
* @param {String} mimetype
* @memberOf XMLHttpRequest
* @since FireFox 2.0
*/
XMLHttpRequest.prototype.overrideMimeType=function(mimetype){};
/**
* function send(body)
* @param {Object} body
* @memberOf XMLHttpRequest
* @since FireFox 2.0
*/
XMLHttpRequest.prototype.send=function(body){};
/**
* function sendAsBinary(body)
* @param {String} body
* @memberOf XMLHttpRequest
* @since FireFox 2.0
*/
XMLHttpRequest.prototype.sendAsBinary=function(body){};
/**
* function setRequestHeader(header,value)
* @param {String} header
* @param {String} value
* @memberOf XMLHttpRequest
* @since FireFox 2.0
*/
XMLHttpRequest.prototype.setRequestHeader=function(header,value){};


/**
* function getAllResponseHeaders()
* @param {String} header
* @type String
* @memberOf XMLHttpRequest
* @since FireFox 2.0
*/
XMLHttpRequest.prototype.getResponseHeader=function(header){return "";};
