/**
 * Visual Blocks Language
 *
 * Copyright 2012 Massachusetts Institute of Technology. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @fileoverview List generators for Blockly, modified for App Inventor
 * @author fraser@google.com (Neil Fraser)
 * @author andrew.f.mckinney@gmail.com (Andrew F. McKinney)
 * Due to the frequency of long strings, the 80-column wrap rule need not apply
 * to language files.
 */

if (!Blockly.Yail) Blockly.Yail = {};

Blockly.Yail = Blockly.Generator.get('Yail');

Blockly.Yail.controls_if = function() {

  var code = "";
  for(var i=0;i<this.elseifCount_ + 1;i++){
    var argument = Blockly.Yail.valueToCode(this, 'IF'+ i, Blockly.Yail.ORDER_NONE) || Blockly.Yail.YAIL_FALSE;
    var branch = Blockly.Yail.statementToCode(this, 'DO'+ i) || Blockly.Yail.YAIL_FALSE;
    if(i != 0) {
      code += Blockly.Yail.YAIL_SPACER + Blockly.Yail.YAIL_BEGIN;
    }
    code += Blockly.Yail.YAIL_IF + argument + Blockly.Yail.YAIL_SPACER + Blockly.Yail.YAIL_BEGIN
      + branch + Blockly.Yail.YAIL_CLOSE_COMBINATION;
  }
  if(this.elseCount_ == 1){
    var branch = Blockly.Yail.statementToCode(this, 'ELSE') || Blockly.Yail.YAIL_FALSE;
    code += Blockly.Yail.YAIL_SPACER + Blockly.Yail.YAIL_BEGIN + branch + Blockly.Yail.YAIL_CLOSE_COMBINATION;
  }
  
  for(var i=0;i<this.elseifCount_;i++){
    code += Blockly.Yail.YAIL_CLOSE_COMBINATION + Blockly.Yail.YAIL_CLOSE_COMBINATION;
  }
  code += Blockly.Yail.YAIL_CLOSE_COMBINATION;
  return code;
};

// [lyn, 01/15/2013] Edited to make consistent with removal of "THEN-DO" and "ELSE-DO"
Blockly.Yail.controls_choose = function() {
  // Choose.
  var test = Blockly.Yail.valueToCode(this, 'TEST', Blockly.Yail.ORDER_NONE)  || Blockly.Yail.YAIL_FALSE;
  var thenReturn = Blockly.Yail.valueToCode(this, 'THENRETURN', Blockly.Yail.ORDER_NONE) || Blockly.Yail.YAIL_FALSE;
  var elseReturn = Blockly.Yail.valueToCode(this, 'ELSERETURN', Blockly.Yail.ORDER_NONE)  || Blockly.Yail.YAIL_FALSE;
  var code = Blockly.Yail.YAIL_IF + test
             + Blockly.Yail.YAIL_SPACER +  thenReturn 
             + Blockly.Yail.YAIL_SPACER +  elseReturn 
             + Blockly.Yail.YAIL_CLOSE_COMBINATION;
  return [code,Blockly.Yail.ORDER_ATOMIC];
};

// [lyn, 12/27/2012]
Blockly.Yail.controls_forEach = function() {
  // For each loop.
  var emptyListCode = Blockly.Yail.YAIL_CALL_YAIL_PRIMITIVE + "make-yail-list" + Blockly.Yail.YAIL_SPACER;
  emptyListCode += Blockly.Yail.YAIL_OPEN_COMBINATION + Blockly.Yail.YAIL_LIST_CONSTRUCTOR + Blockly.Yail.YAIL_SPACER;

  emptyListCode += Blockly.Yail.YAIL_CLOSE_COMBINATION + Blockly.Yail.YAIL_SPACER + Blockly.Yail.YAIL_QUOTE + Blockly.Yail.YAIL_OPEN_COMBINATION;
  emptyListCode += Blockly.Yail.YAIL_CLOSE_COMBINATION;
  emptyListCode += Blockly.Yail.YAIL_SPACER + Blockly.Yail.YAIL_DOUBLE_QUOTE + "make a list" + Blockly.Yail.YAIL_DOUBLE_QUOTE + Blockly.Yail.YAIL_CLOSE_COMBINATION;


  var loopIndexName = this.getTitleValue('VAR');
  var listCode = Blockly.Yail.valueToCode(this, 'LIST', Blockly.Yail.ORDER_NONE) || emptyListCode;
  var bodyCode = Blockly.Yail.statementToCode(this, 'DO', Blockly.Yail.ORDER_NONE) ||  Blockly.Yail.YAIL_FALSE;
  return Blockly.Yail.YAIL_FOREACH + loopIndexName + Blockly.Yail.YAIL_SPACER
         + Blockly.Yail.YAIL_BEGIN + bodyCode + Blockly.Yail.YAIL_CLOSE_COMBINATION + Blockly.Yail.YAIL_SPACER 
         + listCode + Blockly.Yail.YAIL_CLOSE_COMBINATION;
};

// [lyn, 12/27/2012]
Blockly.Yail.controls_forRange = function() {
  // For range loop.
  var loopIndexName = this.getTitleValue('VAR');
  var startCode = Blockly.Yail.valueToCode(this, 'START', Blockly.Yail.ORDER_NONE) || 0;
  var endCode = Blockly.Yail.valueToCode(this, 'END', Blockly.Yail.ORDER_NONE) || 0;
  var stepCode = Blockly.Yail.valueToCode(this, 'STEP', Blockly.Yail.ORDER_NONE) || 0;
  var bodyCode = Blockly.Yail.statementToCode(this, 'DO', Blockly.Yail.ORDER_NONE) || Blockly.Yail.YAIL_FALSE;
  return Blockly.Yail.YAIL_FORRANGE + loopIndexName + Blockly.Yail.YAIL_SPACER
         + Blockly.Yail.YAIL_BEGIN + bodyCode + Blockly.Yail.YAIL_CLOSE_COMBINATION + Blockly.Yail.YAIL_SPACER 
         + startCode + Blockly.Yail.YAIL_SPACER 
         + endCode + Blockly.Yail.YAIL_SPACER 
         + stepCode + Blockly.Yail.YAIL_CLOSE_COMBINATION;
};

Blockly.Yail.for_lexical_variable_get = function() {
  return Blockly.Yail.lexical_variable_get.call(this);
}

Blockly.Yail.controls_while = function() {
  // While condition.
  var test = Blockly.Yail.valueToCode(this, 'TEST', Blockly.Yail.ORDER_NONE) || Blockly.Yail.YAIL_FALSE;
  var toDo = Blockly.Yail.statementToCode(this, 'DO') || Blockly.Yail.YAIL_FALSE;
  var code = Blockly.Yail.YAIL_WHILE + test + Blockly.Yail.YAIL_SPACER + Blockly.Yail.YAIL_BEGIN + toDo + Blockly.Yail.YAIL_CLOSE_COMBINATION + Blockly.Yail.YAIL_CLOSE_COMBINATION;
  return code;
};

// [lyn, 01/15/2013] Added 
Blockly.Yail.controls_do_then_return = function() {
  var stm = Blockly.Yail.statementToCode(this, 'STM', Blockly.Yail.ORDER_NONE) || Blockly.Yail.YAIL_FALSE;
  var value = Blockly.Yail.valueToCode(this, 'VALUE', Blockly.Yail.ORDER_NONE) || Blockly.Yail.YAIL_FALSE;
  var code = Blockly.Yail.YAIL_BEGIN + stm + Blockly.Yail.YAIL_SPACER + value + Blockly.Yail.YAIL_CLOSE_COMBINATION;
  return [code, Blockly.Yail.ORDER_ATOMIC];
};

// [lyn, 01/15/2013] Added 
Blockly.Yail.controls_eval_but_ignore = function() {
  var value = Blockly.Yail.valueToCode(this, 'VALUE', Blockly.Yail.ORDER_NONE) || Blockly.Yail.YAIL_FALSE;
  return Blockly.Yail.YAIL_BEGIN + value + Blockly.Yail.YAIL_SPACER + 'null' + Blockly.Yail.YAIL_CLOSE_COMBINATION;
}

// [lyn, 01/15/2013] Added 
Blockly.Yail.controls_nothing = function() {
  return ['*the-null-value*', Blockly.Yail.ORDER_NONE];
}

Blockly.Yail.controls_openAnotherScreen = function() {
  // Open another screen
  var argument0 = Blockly.Yail.valueToCode(this, 'SCREEN', Blockly.Yail.ORDER_NONE) || null;
  var code = Blockly.Yail.YAIL_CALL_YAIL_PRIMITIVE + "open-another-screen" + Blockly.Yail.YAIL_SPACER;
  code = code + Blockly.Yail.YAIL_OPEN_COMBINATION + Blockly.Yail.YAIL_LIST_CONSTRUCTOR + Blockly.Yail.YAIL_SPACER;
  code = code + argument0 + Blockly.Yail.YAIL_CLOSE_COMBINATION;
  code = code + Blockly.Yail.YAIL_SPACER + Blockly.Yail.YAIL_QUOTE + Blockly.Yail.YAIL_OPEN_COMBINATION;
  code = code + "text" + Blockly.Yail.YAIL_CLOSE_COMBINATION + Blockly.Yail.YAIL_SPACER;
  code = code + Blockly.Yail.YAIL_DOUBLE_QUOTE + "open another screen" + Blockly.Yail.YAIL_DOUBLE_QUOTE + Blockly.Yail.YAIL_CLOSE_COMBINATION;
  return code;
};

Blockly.Yail.controls_openAnotherScreenWithStartValue = function() {
  // Open another screen with start value
  var argument0 = Blockly.Yail.valueToCode(this, 'SCREENNAME', Blockly.Yail.ORDER_NONE) || null;
  var argument1 = Blockly.Yail.valueToCode(this, 'STARTVALUE', Blockly.Yail.ORDER_NONE) || null;
  var code = Blockly.Yail.YAIL_CALL_YAIL_PRIMITIVE + "open-another-screen-with-start-value" + Blockly.Yail.YAIL_SPACER;
  code = code + Blockly.Yail.YAIL_OPEN_COMBINATION + Blockly.Yail.YAIL_LIST_CONSTRUCTOR + Blockly.Yail.YAIL_SPACER;
  code = code + argument0 + Blockly.Yail.YAIL_SPACER + argument1 + Blockly.Yail.YAIL_CLOSE_COMBINATION;
  code = code + Blockly.Yail.YAIL_SPACER + Blockly.Yail.YAIL_QUOTE + Blockly.Yail.YAIL_OPEN_COMBINATION;
  code = code + "text any" + Blockly.Yail.YAIL_CLOSE_COMBINATION + Blockly.Yail.YAIL_SPACER;
  code = code + Blockly.Yail.YAIL_DOUBLE_QUOTE + "open another screen with start value" + Blockly.Yail.YAIL_DOUBLE_QUOTE + Blockly.Yail.YAIL_CLOSE_COMBINATION;
  return code;
};

Blockly.Yail.controls_getStartValue = function() {
  // Get start value
  var code = Blockly.Yail.YAIL_CALL_YAIL_PRIMITIVE + "get-start-value" + Blockly.Yail.YAIL_SPACER;
  code = code + Blockly.Yail.YAIL_OPEN_COMBINATION + Blockly.Yail.YAIL_LIST_CONSTRUCTOR + Blockly.Yail.YAIL_SPACER;
  code = code + Blockly.Yail.YAIL_CLOSE_COMBINATION;
  code = code + Blockly.Yail.YAIL_SPACER + Blockly.Yail.YAIL_QUOTE + Blockly.Yail.YAIL_OPEN_COMBINATION;
  code = code + Blockly.Yail.YAIL_CLOSE_COMBINATION + Blockly.Yail.YAIL_SPACER;
  code = code + Blockly.Yail.YAIL_DOUBLE_QUOTE + "get start value" + Blockly.Yail.YAIL_DOUBLE_QUOTE + Blockly.Yail.YAIL_CLOSE_COMBINATION;
  return [ code, Blockly.Yail.ORDER_ATOMIC ];
};

Blockly.Yail.controls_closeScreen = function() {
  // Close screen
  var code = Blockly.Yail.YAIL_CALL_YAIL_PRIMITIVE + "close-screen" + Blockly.Yail.YAIL_SPACER;
  code = code + Blockly.Yail.YAIL_OPEN_COMBINATION + Blockly.Yail.YAIL_LIST_CONSTRUCTOR + Blockly.Yail.YAIL_SPACER;
  code = code + Blockly.Yail.YAIL_CLOSE_COMBINATION;
  code = code + Blockly.Yail.YAIL_SPACER + Blockly.Yail.YAIL_QUOTE + Blockly.Yail.YAIL_OPEN_COMBINATION;
  code = code + Blockly.Yail.YAIL_CLOSE_COMBINATION + Blockly.Yail.YAIL_SPACER;
  code = code + Blockly.Yail.YAIL_DOUBLE_QUOTE + "close screen" + Blockly.Yail.YAIL_DOUBLE_QUOTE + Blockly.Yail.YAIL_CLOSE_COMBINATION;
  return code;
};

Blockly.Yail.controls_closeScreenWithValue = function() {
  // Close screen with value
  var argument0 = Blockly.Yail.valueToCode(this, 'SCREEN', Blockly.Yail.ORDER_NONE) || null;
  var code = Blockly.Yail.YAIL_CALL_YAIL_PRIMITIVE + "close-screen-with-value" + Blockly.Yail.YAIL_SPACER;
  code = code + Blockly.Yail.YAIL_OPEN_COMBINATION + Blockly.Yail.YAIL_LIST_CONSTRUCTOR + Blockly.Yail.YAIL_SPACER;
  code = code + argument0 + Blockly.Yail.YAIL_CLOSE_COMBINATION;
  code = code + Blockly.Yail.YAIL_SPACER + Blockly.Yail.YAIL_QUOTE + Blockly.Yail.YAIL_OPEN_COMBINATION;
  code = code + "any" + Blockly.Yail.YAIL_CLOSE_COMBINATION + Blockly.Yail.YAIL_SPACER;
  code = code + Blockly.Yail.YAIL_DOUBLE_QUOTE + "close screen with value" + Blockly.Yail.YAIL_DOUBLE_QUOTE + Blockly.Yail.YAIL_CLOSE_COMBINATION;
  return code;
};

Blockly.Yail.controls_closeApplication = function() {
  // Close application
  var code = Blockly.Yail.YAIL_CALL_YAIL_PRIMITIVE + "close-application" + Blockly.Yail.YAIL_SPACER;
  code = code + Blockly.Yail.YAIL_OPEN_COMBINATION + Blockly.Yail.YAIL_LIST_CONSTRUCTOR + Blockly.Yail.YAIL_SPACER;
  code = code + Blockly.Yail.YAIL_CLOSE_COMBINATION;
  code = code + Blockly.Yail.YAIL_SPACER + Blockly.Yail.YAIL_QUOTE + Blockly.Yail.YAIL_OPEN_COMBINATION;
  code = code + Blockly.Yail.YAIL_CLOSE_COMBINATION + Blockly.Yail.YAIL_SPACER;
  code = code + Blockly.Yail.YAIL_DOUBLE_QUOTE + "close application" + Blockly.Yail.YAIL_DOUBLE_QUOTE + Blockly.Yail.YAIL_CLOSE_COMBINATION;
  return code;
};

Blockly.Yail.controls_getPlainStartText = function() {
  // Get plain start text
  var code = Blockly.Yail.YAIL_CALL_YAIL_PRIMITIVE + "get-plain-start-text" + Blockly.Yail.YAIL_SPACER;
  code = code + Blockly.Yail.YAIL_OPEN_COMBINATION + Blockly.Yail.YAIL_LIST_CONSTRUCTOR + Blockly.Yail.YAIL_SPACER;
  code = code + Blockly.Yail.YAIL_CLOSE_COMBINATION;
  code = code + Blockly.Yail.YAIL_SPACER + Blockly.Yail.YAIL_QUOTE + Blockly.Yail.YAIL_OPEN_COMBINATION;
  code = code + Blockly.Yail.YAIL_CLOSE_COMBINATION + Blockly.Yail.YAIL_SPACER;
  code = code + Blockly.Yail.YAIL_DOUBLE_QUOTE + "get plain start text" + Blockly.Yail.YAIL_DOUBLE_QUOTE + Blockly.Yail.YAIL_CLOSE_COMBINATION;
  return [ code, Blockly.Yail.ORDER_ATOMIC ];
};

Blockly.Yail.controls_closeScreenWithPlainText = function() {
  // Close screen with plain text
  var argument0 = Blockly.Yail.valueToCode(this, 'TEXT', Blockly.Yail.ORDER_NONE) || Blockly.Yail.YAIL_FALSE;
  var code = Blockly.Yail.YAIL_CALL_YAIL_PRIMITIVE + "close-screen-with-plain-text" + Blockly.Yail.YAIL_SPACER;
  code = code + Blockly.Yail.YAIL_OPEN_COMBINATION + Blockly.Yail.YAIL_LIST_CONSTRUCTOR + Blockly.Yail.YAIL_SPACER;
  code = code + argument0 + Blockly.Yail.YAIL_CLOSE_COMBINATION;
  code = code + Blockly.Yail.YAIL_SPACER + Blockly.Yail.YAIL_QUOTE + Blockly.Yail.YAIL_OPEN_COMBINATION;
  code = code + "text" + Blockly.Yail.YAIL_CLOSE_COMBINATION + Blockly.Yail.YAIL_SPACER;
  code = code + Blockly.Yail.YAIL_DOUBLE_QUOTE + "close screen with plain text" + Blockly.Yail.YAIL_DOUBLE_QUOTE + Blockly.Yail.YAIL_CLOSE_COMBINATION;
  return code;
};
