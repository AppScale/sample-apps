/* You may obtain a copy of the License at
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
 * @fileoverview Generating Yail for catagories of blocks.
 * @author andrew.f.mckinney@gmail.com (Andrew F. McKinney) Due to the frequency
 *         of long strings, the 80-column wrap rule need not apply to language
 *         files.
 */

Blockly.Yail = Blockly.Generator.get('Yail');

// Variable Blocks

// Global variable definition block
Blockly.Yail.global_declaration = function() {
  var name = this.getTitleValue('NAME');
  var argument0 = Blockly.Yail.valueToCode(this, 'VALUE', Blockly.Yail.ORDER_NONE) || '0';
  var code = Blockly.Yail.YAIL_DEFINE + name + Blockly.Yail.YAIL_SPACER + argument0 + Blockly.Yail.YAIL_CLOSE_COMBINATION;
  return code;
};

// Global variable getter block
Blockly.Yail.lexical_variable_get = function() {
  var code = "";
  var name = this.getTitleValue('VAR');
  
  var commandAndName = Blockly.Yail.getVariableCommandAndName(name);
  code += commandAndName[0];
  name = commandAndName[1];
  
  code += name + Blockly.Yail.YAIL_CLOSE_COMBINATION;
  return [ code, Blockly.Yail.ORDER_ATOMIC ];
};

// Global variable setter block
Blockly.Yail.lexical_variable_set = function() {
  var argument0 = Blockly.Yail.valueToCode(this, 'VALUE', Blockly.Yail.ORDER_NONE) || '0';
  var code = "";
  var varName = this.getTitleValue('VAR');
  var commandAndName = Blockly.Yail.setVariableCommandAndName(varName);
  code += commandAndName[0];
  name = commandAndName[1];
  
  code += name + Blockly.Yail.YAIL_SPACER + argument0
      + Blockly.Yail.YAIL_CLOSE_COMBINATION;
  return code;
};

// [lyn, 12/27/2012] Handle prefixes abstractly
Blockly.Yail.getVariableCommandAndName = function(name){
  var command = "";
  var pair = Blockly.unprefixName(name);
  var prefix = pair[0];
  var unprefixedName = pair[1];
  if (prefix === Blockly.globalNamePrefix) {
    name = unprefixedName;
    command = Blockly.Yail.YAIL_GET_VARIABLE;
  } else {
    name = (Blockly.possiblyPrefixYailNameWith(prefix))(unprefixedName);
    command = Blockly.Yail.YAIL_LEXICAL_VALUE;
  }
  return [command,name]
}

// [lyn, 12/27/2012] New
Blockly.Yail.setVariableCommandAndName = function(name){
  var command = "";
  var pair = Blockly.unprefixName(name);
  var prefix = pair[0];
  var unprefixedName = pair[1];
  if (prefix === Blockly.globalNamePrefix) {
    name = unprefixedName;
    command = Blockly.Yail.YAIL_SET_VARIABLE;
  } else {
    name = (Blockly.possiblyPrefixYailNameWith(prefix))(unprefixedName);
    command = Blockly.Yail.YAIL_SET_LEXICAL_VALUE;
  }
  return [command,name]
}

Blockly.Yail.local_declaration_statement = function() {
  return Blockly.Yail.local_variable(this,false);
}

Blockly.Yail.local_declaration_expression = function() {
  return Blockly.Yail.local_variable(this,true);
}

Blockly.Yail.local_variable = function(block,isExpression) {
  var code = Blockly.Yail.YAIL_LET;
  code += Blockly.Yail.YAIL_OPEN_COMBINATION + Blockly.Yail.YAIL_SPACER;
  for(var i=0;block.getTitleValue("VAR" + i);i++){
    code += Blockly.Yail.YAIL_OPEN_COMBINATION + (Blockly.usePrefixInYail ? "local_" : "") + block.getTitleValue("VAR" + i);
    code += Blockly.Yail.YAIL_SPACER + ( Blockly.Yail.valueToCode(block, 'DECL' + i, Blockly.Yail.ORDER_NONE) || '0' );
    code += Blockly.Yail.YAIL_CLOSE_COMBINATION + Blockly.Yail.YAIL_SPACER;
  }
  code += Blockly.Yail.YAIL_SPACER +  Blockly.Yail.YAIL_CLOSE_COMBINATION;
  // [lyn, 01/15/2013] Added to fix bug in local declaration expressions:
  if(isExpression){
    if(!block.getInputTargetBlock("RETURN")){
      code += Blockly.Yail.YAIL_SPACER +  "0";
    } else {
      code += Blockly.Yail.YAIL_SPACER +  Blockly.Yail.valueToCode(block, 'RETURN', Blockly.Yail.ORDER_NONE);
    }
  } else {
    code += Blockly.Yail.YAIL_SPACER +  Blockly.Yail.statementToCode(block, 'STACK', Blockly.Yail.ORDER_NONE);
  }
  code += Blockly.Yail.YAIL_SPACER +  Blockly.Yail.YAIL_CLOSE_COMBINATION;
  if(!isExpression){
    return code;
  } else {
    return [ code, Blockly.Yail.ORDER_ATOMIC ];
  }
};
