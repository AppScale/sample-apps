/**
 * Blockly Demo: Maze
 *
 * Copyright 2012 Google Inc.
 * http://code.google.com/p/blockly/
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
 * @fileoverview Demonstration of Blockly: Solving a maze.
 * @author fraser@google.com (Neil Fraser)
 */
'use strict';

// Extensions to Blockly's language and JavaScript generator.

Blockly.JavaScript = Blockly.Generator.get('JavaScript');

Blockly.Language.maze_move = {
  // Block for moving forward or backwards.
  category: 'Commands',
  helpUrl: 'http://code.google.com/p/blockly/wiki/Move',
  init: function() {
    this.setColour(290);
    this.appendDummyInput()
        .appendTitle('move')
        .appendTitle(new Blockly.FieldDropdown(this.DIRECTIONS), 'DIR');
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setTooltip('Moves Pegman forward or backward one space.');
  }
};

Blockly.Language.maze_move.DIRECTIONS =
    [['forward', 'moveForward'], ['backward', 'moveBackward']];

Blockly.JavaScript.maze_move = function() {
  // Generate JavaScript for moving forward or backwards.
  return 'Maze.' + this.getTitleValue('DIR') + '("' + this.id + '");\n';
};

Blockly.Language.maze_turnLeft = {
  // Block for turning left or right.
  category: 'Commands',
  helpUrl: 'http://code.google.com/p/blockly/wiki/Turn',
  init: function() {
    this.setColour(290);
    this.appendDummyInput()
        .appendTitle('turn')
        .appendTitle(new Blockly.FieldDropdown(this.DIRECTIONS), 'DIR');
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setTooltip('Turns Pegman left or right by 90 degrees.');
  }
};

Blockly.Language.maze_turnLeft.DIRECTIONS =
    [['left', 'turnLeft'], ['right', 'turnRight'], ['randomly', 'random']];

Blockly.Language.maze_turnRight = {
  // Block for turning left or right.
  category: 'Commands',
  helpUrl: null,
  init: function() {
    this.setColour(290);
    this.appendDummyInput()
        .appendTitle('turn')
        .appendTitle(new Blockly.FieldDropdown(
                     Blockly.Language.maze_turnLeft.DIRECTIONS), 'DIR');
    this.setTitleValue(Blockly.Language.maze_turnLeft.DIRECTIONS[1][1], 'DIR');
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setTooltip('Turns Pegman left or right by 90 degrees.');
  }
};

Blockly.JavaScript.maze_turnLeft = function() {
  // Generate JavaScript for turning left or right.
  var dir = this.getTitleValue('DIR');
  var code;
  if (dir == 'random') {
    code = 'if (Math.random() < 0.5) {\n' +
           '  Maze.turnLeft("' + this.id + '");\n' +
           '} else {\n' +
           '  Maze.turnRight("' + this.id + '");\n' +
           '}\n';
  } else {
    code = 'Maze.' + dir + '("' + this.id + '");\n';
  }
  return code;
};

// Turning left and right use the same code.
Blockly.JavaScript.maze_turnRight = Blockly.JavaScript.maze_turnLeft;

Blockly.Language.maze_isWall = {
  // Block for checking if there a wall.
  category: 'Logic',
  helpUrl: 'http://code.google.com/p/blockly/wiki/Wall',
  init: function() {
    this.setColour(120);
    this.setOutput(true, Boolean);
    this.appendDummyInput()
        .appendTitle('wall')
        .appendTitle(new Blockly.FieldDropdown(this.DIRECTIONS), 'DIR');
    this.setTooltip('Returns true if there is a wall\n' +
                    'in the specified direction.');
  }
};

Blockly.Language.maze_isWall.DIRECTIONS =
    [['ahead', 'isWallForward'],
     ['to the left', 'isWallLeft'],
     ['to the right', 'isWallRight'],
     ['behind', 'isWallBackward']];

Blockly.JavaScript.maze_isWall = function() {
  // Generate JavaScript for checking if there is a wall.
  var code = 'Maze.' + this.getTitleValue('DIR') + '()';
  return [code, Blockly.JavaScript.ORDER_FUNCTION_CALL];
};

Blockly.Language.controls_forever = {
  // Do forever loop.
  category: 'Logic',
  helpUrl: 'http://code.google.com/p/blockly/wiki/Repeat',
  init: function() {
    this.setColour(120);
    this.appendDummyInput()
        .appendTitle('repeat until finished');
    this.appendStatementInput('DO').appendTitle('do');
    this.setPreviousStatement(true);
    this.setTooltip('Repeat the enclosed steps until finish point is reached.');
  }
};

Blockly.JavaScript.controls_forever = function() {
  // Generate JavaScript for do forever loop.
  var branch0 = Blockly.JavaScript.statementToCode(this, 'DO');
  return 'while (true) {\n' + branch0 +
      '  Maze.checkTimeout("' + this.id + '");\n}\n';
};

Blockly.JavaScript.controls_whileUntil = function() {
  // Do while/until loop.
  var argument0 = Blockly.JavaScript.valueToCode(this, 'BOOL',
      Blockly.JavaScript.ORDER_NONE) || 'false';
  var branch0 = Blockly.JavaScript.statementToCode(this, 'DO');
  if (this.getTitleValue('MODE') == 'UNTIL') {
    if (!argument0.match(/^\w+$/)) {
      argument0 = '(' + argument0 + ')';
    }
    argument0 = '!' + argument0;
  }
  return 'while (' + argument0 + ') {\n' + branch0 +
      '  Maze.checkTimeout("' + this.id + '");\n}\n';
};

function init() {
  // Whitelist of blocks to keep.
  var newLanguage = {};
  var keepers = ['maze_move', 'maze_turnLeft', 'maze_turnRight',
      'maze_isWall', 'controls_if', 'controls_if_if', 'controls_if_elseif',
      'controls_if_else', 'controls_forever', 'controls_whileUntil',
      'logic_operation', 'logic_negate'];
  for (var x = 0; x < keepers.length; x++) {
    newLanguage[keepers[x]] = Blockly.Language[keepers[x]];
  }
  // Fold control category into logic category.
  for (var name in newLanguage) {
    if (newLanguage[name].category == 'Control') {
      newLanguage[name].category = 'Logic';
    }
  }
  Blockly.Language = newLanguage;

  Blockly.inject(document.body, {path: '../../'});

  if (window.parent.Maze) {
    // Let the top-level application know that Blockly is ready.
    window.parent.Maze.init(Blockly);
  } else {
    // Attempt to diagnose the problem.
    var msg = 'Error: Unable to communicate between frames.\n\n';
    if (window.parent == window) {
      msg += 'Try loading index.html instead of frame.html';
    } else if (window.location.protocol == 'file:') {
      msg += 'This may be due to a security restriction preventing\n' +
          'access when using the file:// protocol.\n' +
          'http://code.google.com/p/chromium/issues/detail?id=47416';
    }
    alert(msg);
  }
}
