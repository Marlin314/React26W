// code styler for java.

decWords = []; // keywords are assigned to span classes for word decoration
clsForWords('JC', 'static class abstract throws interface extends implements instanceof Override import package');
clsForWords('JG','if else for while switch case default try catch finally');
clsForWords('JX','break throw goto continue return');
clsForWords('JTF','true false null final');
clsForWords('JN','new native strictfp synchronized transient volatile');
clsForWords('JV','void this super');
clsForWords('JPR','int boolean float double long char byte short');
clsForWords('JPP','public private protected');

function decorate(word){
  if(word in decWords) return '<span class="JKW '+decWords[word]+'">'+word+'</span>';
  if(/^[A-Z_0-9]+$/.test(word))return '<span class="JCAPS">'+word+'</span>';
  if(/[A-Z]/.test(word.charAt(0))) return '<span class="JFirstCap">'+word+'</span>';
  return '<span class="Jword">'+word+'</span>';
}
function clsForWords(cls, words){var w = words.split(' ');for(var i = 0; i<w.length; i++){decWords[w[i]] = cls;}}

//fst - finite state transformer for Java
function javaToHtml(str){
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
