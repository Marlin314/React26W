// code styler for javascript.
//alert("loading jsToHTM");

decWords = []; // keywords are assigned to span classes for word decoration
clsForWords('JC', 'prototype var function');
clsForWords('JG','if else for while switch case default');
clsForWords('JX','break continue return');
clsForWords('JTF','true false null this');
clsForWords('JN','new');
//MYSQL KEYWORDS
clsForWords('MKS', 'SELECT FROM JOIN LEFT RIGHT WHERE ORDER BY INSERT INTO VALUES UPDATE SET VARCHAR INT CREATE TABLE DATABASE USE SHOW TABLES ASC DESC DELETE DROP PRIMARY KEY FOREIGN AUTO_INCREMENT UNION LANGUAGE LEVEL LESS LIKE LIMIT YEAR MONTH DAY OWNER PAGE PASSWORD DATE DAY DATETIME QUARTER SQL');

function decorate(word){
  if(word in decWords) return '<span class="JKW '+decWords[word]+'">'+word+'</span>';
  if(/^[A-Z_0-9]+$/.test(word))return '<span class="JCAPS">'+word+'</span>';
  if(/[A-Z]/.test(word.charAt(0))) return '<span class="JFirstCap">'+word+'</span>';
  return '<span class="Jword">'+word+'</span>';
}
function clsForWords(cls, words){var w = words.split(' ');for(var i = 0; i<w.length; i++){decWords[w[i]] = cls;}}

//fst - finite state transformer for JS - very similar to FST for Java (regex is only real addition
function jsToHtml(str){
  var res = '<pre><div class="js">';
  var word = '', ws = 'n'; // word state & last accumulated word.
  var cs = 'J'; // code state
  for(var i=0; i<str.length; i++){
    var c = str.charAt(i); var alpha = /[A-Za-z]/.test(c); var dig = /[0-9]/.test(c);
    // we can entity encode c now because no entity char is involved in any of the parse states
    c = (c == '&')? '&amp;':c;  c = (c=='<')?'&lt;':c; c = (c=='>')?'&gt;':c;

    /* States - We have a couple of levels of CodeState, cs = JBEQT for JS, Block_comment, Eol_comment, Quoted, Ticked
       and intermediate states jbqt for JS+/, Block+*, Quote+\, T+\
       most states lapse back to themselves and intermediate states lapse back to their parent.

       Within the JS default state ('J') (i.e. when you are not detecting quotes or comment transitions)
       we have a second state, ws, the word state (n or w), to determine whether we should or should not accumulate a word.
       basically on an n to w transtion we start accumulating a word and on a w to n we have found
       a keyword or a name that we could decorate.
     */
    switch(cs){ // FST for js Regexps NOT implemented
      case 'J': switch(c){
        case '/':  cs = 'j'; if(ws=='w'){ws = 'n'; res += decorate(word);} break;
        case '"':  cs = 'Q'; if(ws=='w'){ws = 'n'; res += decorate(word);} res += '<span class="JQ">' + c; break;
        case '\'': cs = 'T'; if(ws=='w'){ws = 'n'; res += decorate(word);} res += '<span class="JT">' + c; break;
        default: switch(ws){
          case 'n': if(alpha){ws = 'w'; word = c;} else {res += c;} break;
          case 'w': if(alpha || dig || c == '_'){word += c;} else {ws = 'n'; res += decorate(word) + c; } break;
        }
        cs = 'J';
      }break;
      case 'j': switch(c){
        case '/':  cs = 'E'; res += '<span class="JE">//'; break;
        case '*':  cs = 'B'; res += '<div class="JB">/*'; break;
        default: cs = 'J'; res += '/'+c; // back to Java if we didn't get the second char
      }break;
      case 'B': switch(c){
        case '*':  cs = 'b'; res += c; break;
        default: cs = 'B'; res += c
      }break;
      case 'b': switch(c){
        case '/':  cs = 'J'; res += c + '</div>'; break;
        default: cs = 'B'; res += c;
      }break;
      case 'E': switch(c){
        case '\r':  cs = 'J'; res += '</span>'+ c; break;
        default: cs = 'E'; res += c;
      }break;
      case 'Q': switch(c){
        case '\\':  cs = 'q'; res += c; break;
        case '"':   cs = 'J'; res += c + '</span>'; break;
        default: cs = 'Q'; res += c;
      }break;
      case 'q': switch(c){
        default: cs = 'Q'; res += c;
      }break;
      case 'T': switch(c){
        case '\\':  cs = 't'; res += c; break;
        case "'":   cs = 'J'; res += c + '</span>'; break;
        default: cs = 'T'; res += c;
      }break;
      case 't': switch(c){
        default: cs = 'T'; res += c;
      }break;
    } // end of fst


  } // end of for loop for characters
  if(ws == 'w'){res += decorate(word);} // flush the word buffer
  return res + '</div></pre>';
}

// FST for MySql
function mysqlToHtml(str){
  var res = '<pre><div class="java">';
  var word = '', ws = 'n'; // word state & last accumulated word.
  var cs = 'J'; // code state
  for(var i=0; i<str.length; i++){
    var c = str.charAt(i); var alpha = /[A-Za-z]/.test(c); var dig = /[0-9]/.test(c);
    // we can entity encode c now because no entity char is involved in any of the parse states
    c = (c == '&')? '&amp;':c;  c = (c=='<')?'&lt;':c; c = (c=='>')?'&gt;':c;

    /* States - We have a couple of levels of CodeState, cs = JBEQT for Java, Block_comment, Eol_comment, Quoted, Ticked
       and intermediate states jbqt for Java+/, Block+*, Quote+\, T+\
       most states lapse back to themselves and intermediate states lapse back to their parent.

       Within the Java default state ('J') (i.e. when you are not detecting quotes or comment transitions)
       we have a second state, ws, the word state (n or w), to determine whether we should or should not accumulate a word.
       basically on an n to w transtion we start accumulating a word and on a w to n we have found
       a keyword or a name that we could decorate.
     */
    switch(cs){ // FST for java
      case 'J': switch(c){
        case '/':  cs = 'j'; if(ws=='w'){ws = 'n'; res += decorate(word);} break; // to detect /* in SQL
        case '-':  cs = 'j'; if(ws=='w'){ws = 'n'; res += decorate(word);} break; // to detect -- in SQL
        case '#':  cs = 'E'; if(ws=='w'){ws = 'n'; res += decorate(word);}
             cs = 'E'; res += '<span class="JE">//';
             break;
        case '"':  cs = 'Q'; if(ws=='w'){ws = 'n'; res += decorate(word);} res += '<span class="JQ">' + c; break;
        case '\'': cs = 'T'; if(ws=='w'){ws = 'n'; res += decorate(word);} res += '<span class="JT">' + c; break;
        default: switch(ws){
          case 'n': if(alpha){ws = 'w'; word = c;} else {res += c;} break;
          case 'w': if(alpha || dig || c == '_'){word += c;} else {ws = 'n'; res += decorate(word) + c; } break;
        }
        cs = 'J';
      }break;

      /* OK technically I am introducing a bug into this MySQL interpreter at this point.
         I don't want to waste time fixing it because I don't think it matters. MySQL used
         C style block comments but does their EOL comments differently. They use # or -- for EOL.
         In C and Java state 'j' means I have seen a / and am next distinguishing between //, /*,
         or anything else. In MySQL, j means that I have seen either a / leading to a /* or have
         seen a - leading to a --. This is technically wrong because in this next block of code
         I ignore how I got into state 'j'. Thus if I type -* it will look like /* and if I type
         /- it will look like --. I don't believe that either of those expressions, -* or /- are
         legal MYSQL so I will not worry about improperly decorating them in this code. And even if
         I do decorate them wrong. Who cares? No one reads my online texts anyway!
      */
      case 'j': switch(c){
        case '-':  cs = 'E'; res += '<span class="JE">--'; break; // MYSQL uses -- or # for EOL
        case '*':  cs = 'B'; res += '<div class="JB">/*'; break;  // MYSQL does use /* */ block comments
        default: cs = 'J'; res += '/'+c; // back to Java if we didn't get the second char
      }break;
      case 'B': switch(c){
        case '*':  cs = 'b'; res += c; break;
        default: cs = 'B'; res += c
      }break;
      case 'b': switch(c){
        case '/':  cs = 'J'; res += c + '</div>'; break;
        default: cs = 'B'; res += c;
      }break;
      case 'E': switch(c){
        case '\r':  cs = 'J'; res += '</span>'+ c; break;
        default: cs = 'E'; res += c;
      }break;
      case 'Q': switch(c){
        case '\\':  cs = 'q'; res += c; break;
        case '"':   cs = 'J'; res += c + '</span>'; break;
        default: cs = 'Q'; res += c;
      }break;
      case 'q': switch(c){
        default: cs = 'Q'; res += c;
      }break;
      case 'T': switch(c){
        case '\\':  cs = 't'; res += c; break;
        case "'":   cs = 'J'; res += c + '</span>'; break;
        default: cs = 'T'; res += c;
      }break;
      case 't': switch(c){
        default: cs = 'T'; res += c;
      }break;
    } // end of fst


  } // end of for loop for characters
  if(ws == 'w'){res += decorate(word);} // flush the word buffer
  return res + '</div></pre>';

}

