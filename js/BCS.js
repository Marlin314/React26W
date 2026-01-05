// Css classes and nesting
// BookMenu - BookTog
// SectionMenu - Names, Buttons
// Names - BookTog, ChapterName, SectionName
// chapters (is an ol) with spans - cName, sLink

//alert("Loading BCS");

function inject(loc, val){document.getElementById(loc).innerHTML = val;}
function startBook(){inject('stuff', theBook.bookView());} // we typically start in the Table of Contents

var nCurChapt = 0; nCurSect = 0; // start at the begining
var theBook;
var viewType = 0; // 0 is book view, 1 is section view

function Book(title){this.title = title; this.chapters = [];}

Book.prototype.addChapter = function(c){c.nChapt = this.chapters.length; this.chapters.push(c);}
Book.prototype.bookView = function(){
  var res = this.bookViewMenu() + '<ol class="chapters">';
  for(var i = 0; i<this.chapters.length; i++){ res += '<li>' + this.chapters[i].bookView() + '</li>';}
  res += '</ol>';
  res += '<hr/>This is the Table of Contents, (TOC), for the book, <i>'+this.title+'</i>. The TOC provides you with buttons so that you can directly jump to any section in the book. You can always get to the TOC during your reading by clicking on the name of the book up in the menubar. That button is actually a toggle that swaps you back and forth between your current reading place in the book and the TOC.<p> The next and prev button (also in the menubar once you leave the TOC) allow you to move forward and backward through the book from your current reading place. If you use the TOC to jump to new place in the book that becomes your current reading place and your old place is lost. There are no bookmarks, or undo, or any other fancy controls. This is just a simple book reader with 3 buttons - next, prev, and TOC. So click on the first section of the first chapter to get started on <i>'+this.title+'</i>. Enjoy!</p>';
  return res;
}
Book.prototype.go = function(nC, nS){this.chapters[(nC==-1)?this.chapters.length - 1 : nC].go(nS);}
Book.prototype.bookViewMenu = function(){return '<div class="BookMenu">'+this.bookTog()+'<div class="ClearFloats"></div></div>';}
Book.prototype.bookTog = function(){return '<div class="BookTog" onClick="bookTog();">'+this.title+'</div>';}

function Chapter(title){this.title = title; this.sections = []; this.nChapt = -1;}
Chapter.prototype.addSection = function(s){s.nChapt = this.nChapt; s.nSect = this.sections.length; this.sections.push(s);}
Chapter.prototype.bookView = function(){
  var res = '<span class="cName">'+this.title+'</span> - '; var sep = '';
  for(var i = 0; i<this.sections.length; i++){res += sep + this.sections[i].asLink(); sep = ', ';}
  return res;
}
Chapter.prototype.go = function(nS){nCurChapt = this.nChapt; this.sections[(nS==-1)?this.sections.length - 1 : nS].go();}
Chapter.prototype.titleDiv = function(){return '<div class="ChapterName">'+(this.nChapt+1) +" "+this.title+'</div>';}

function Section(title, contents, tags){
  this.title = title; this.contents = contents; this.tags = tags;
  this.nChapt = -1; this.nSect = -1;
}
Section.prototype.titleDiv = function(){return '<div class="SectionName">'+(this.nSect+1) +" "+this.title+'</div><div class="ClearFloats"></div>';}
Section.prototype.asLink = function(){
  return '<span class="sLink" onClick="theBook.go('+this.nChapt+', '+this.nSect+');">'+this.title+'</span>';
}
Section.prototype.go = function(){
  nCurSect = this.nSect; viewType = 1;
  inject('stuff', this.fullSectionMenu() + this.sectionContents() + this.partialSectionMenu());
  window.scrollTo(0,0); // added 2/23/2023
}
Section.prototype.sectionContents = function(){return '<div class="SectionContents">'+this.contents+'</div>';}
Section.prototype.fullSectionMenu = function(){
  return '<div class="SectionMenu">' + '<div class="Buttons">' + bPrev() + bNext() + '</div>' + this.namesDiv() + '</div>' ;
}
Section.prototype.partialSectionMenu = function(){
  return '<div class="SectionMenu">' + '<div class="Buttons">' + bPrev() + bNext() + '</div><div class="ClearFloats"></div></div>' ;
}
Section.prototype.namesDiv = function(){
  return '<div class="Names">'+theBook.bookTog()+ theBook.chapters[nCurChapt].titleDiv() + this.titleDiv() + '</div>';
}

var restartMsg = ''
function bPrev(){return '<button type="button" class="ButtonStyle" onClick="prev();">PREV</button>';}
function bNext(){return '<button type="button" class="ButtonStyle" onClick="next();">NEXT</button> '+restartMsg;}
function next(){
  if(nCurSect < theBook.chapters[nCurChapt].sections.length - 1){
    theBook.go(nCurChapt, nCurSect + 1);
  } else {
    nextChapt();
  }
  restartMsg = '';
}
function prev(){
  if(nCurSect > 0){
    theBook.go(nCurChapt, nCurSect - 1);
  } else {
    prevChapt();
  }
  restartMsg = '';
}
function bookTog(){viewType = 1 - viewType; if(viewType == 0){inject('stuff', theBook.bookView());} else {theBook.go(nCurChapt, nCurSect);}}
function nextChapt(){
  if(nCurChapt < theBook.chapters.length - 1){
    theBook.go(nCurChapt + 1, 0);
  } else {
    restartMsg = "<div class=\"restart\">You finished the book and were returned to the start.</div>";
    theBook.go(0,0);
  }
}
function prevChapt(){if(nCurChapt > 0){theBook.go(nCurChapt - 1, -1);} else {theBook.go(-1,-1);}}

//-------------------Markdown------------------------

function getMarkdown(loc){
  var md = document.getElementById(loc).innerHTML.split('\n');
  //alert(md.length + " chunks");
  for(var i = 0; i<md.length; i++){
    processChunk(md[i]);
  }
  flushSection();
}

var blockOfCode = 0;
//var startOfCodeBlock = '<pre><div class="CODE">', endOfCodeBlock = '</div></pre>';

var listType = 'u';
function processChunk(chunk){
  var type = chunk.substring(0,2); // first two chars are significant
  // we need to detect transitions in and out of sequences of code.
  if(blockOfCode == 0 && type == '  '){blockOfCode = '';}
  if(blockOfCode != 0 && type != '  '){sectionContents += curIndentEncoder(blockOfCode) ; blockOfCode = 0; }

  switch(type){
    case '::' : bookChunk(chunk); break;
    case '++' : heading('h1', chunk); break;
    case '==' : heading('h2', chunk); break;
    case '--' : heading('h3', chunk); break;
    case '  ' : codeChunk(chunk); break;
    case '0.' : sectionContents += '<ul><li>' + bangSty(chunk.substring(2)); listType = 'u'; break;
    case '1.' : sectionContents += '<ol><li>' + bangSty(chunk.substring(2)); listType = 'o'; break;
    case '2.' : sectionContents += '</li><li>' + bangSty(chunk.substring(2)); break;
    case '3.' : sectionContents += '</li></'+listType+'l>'; break;
    case '[[' : note(bangSty(chunk)); break;
    default : paragraph(bangSty(chunk));
  }
}

function bangSty(str){ // globally replaces ' !xfoo_bar' with ' <span class="x">foo bar</span>'

  var res = str.replace(/( !\S+)/g,
    function(){
      var cl = arguments[1].charAt(2);
      var inner = arguments[1].substring(3);
      var lastc = inner.charAt(inner.length-1);
      if (".,!?:;".indexOf(lastc) < 0) { // chop off lastc if it is sentence punctuation
        lastc = '';
      } else {
        inner =  inner.substring(0,inner.length-1);
      }
      inner = inner.replace(/_/g,' ');
      return ' <span class="'+cl+'">'+inner+'</span>'+lastc;}
  );

  return res
}



var currentChapter = '', sectionName = 'you have an ordering problem', sectionContents = '';

function flushSection(){
  if(sectionContents != ''){
    currentChapter.addSection(new Section(sectionName, sectionContents));
    sectionName = 'you have an ordering problem'; sectionContents = '';
  }
}

function vanillaTextEncoder(str){return '<pre><div class="inText">' + entity(blockOfCode) + '</div></pre>';}
var indentEncoders = []; indentEncoders['text'] = vanillaTextEncoder;
var curIndentEncoder = indentEncoders['text'];

function bookChunk(chunk){
  var type = chunk.substring(0,4), val = chunk.substring(4);
  switch(type){
    case '::B ' : theBook = new Book(val); sectionContents = ''; break;
    case '::C ' : flushSection(); theBook.addChapter(currentChapter = new Chapter(val)); break;
    case '::S ' : flushSection(); sectionName = val; break;
    case '::I ' : setIndentEncoder(val.trim()); break; // we can re-assign the text encoder
    default: alert('Your book structure tag is not correct - ' + chunk);
  }
}
function setIndentEncoder(foo){
  if(foo in indentEncoders){curIndentEncoder = indentEncoders[foo];} else {alert("No such Encoder: ::I " + foo);}
}


function heading(tag, ch){sectionContents += '<'+tag+'>'+ch.substring(2)+'</'+tag+'>';}
function paragraph(chunk){if(chunk != ''){sectionContents += '<p>'+chunk+'</p>';}}
function note(chunk){sectionContents += '<div class="NOTE"><p>'+chunk+']]</p></div>';}

function codeChunk(chunk){blockOfCode += chunk + '\r';}

function entity(str){
  //var t = str.replace(/ENDSCRIPT/g, '</script>');
  var t = str.replace(/<js(.*?)>/g, '<script$1>').replace(/<\/js>/g, '</script>' );
  return t.replace(/&/g,'&amp;').replace(/>/g,'&gt;').replace(/</g,'&lt;').replace(/ /g,'&nbsp;');
}
function jformat(str){return '<code>'+entity(str)+'</code>';}