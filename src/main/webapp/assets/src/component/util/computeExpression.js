define(function (require, exports, module) {
    var atomMap = {
        NUMBER: 0,
        PARAM: 1,

        OPERATION: 10,
        MULTI: 11,
        DIV: 12,
        PLUS: 13,
        MINUS: 14,

        COMPARE: 20,
        EQUAL: 21,
        GREATER: 22,
        GREATEROREQUAL: 23,
        LESS: 24,
        LESSOREQUAL: 25,

        LOGIC: 30,
        LOGICAND: 31,
        LOGICOR: 32,
        LOGICNOT: 33,

        LPAREN: 40,
        RPAREN: 41,

        SUBLIST: 1000
    };
    var getErr = function (index) {
        return { message: 'error at ' + index.toString(), error: true };
    };
    var isDigit = function (char) {
        return '0123456789.'.indexOf(char) >= 0;
    };
    var isParam = function (char) {
        return /^[A-Z]{1}$/.test(char);
    };
    var isOperat = function (char) {
        return '+-*/%'.indexOf(char) >= 0;
    };
    var isCompare = function (char) {
        return '=><!'.indexOf(char) >= 0;
    };
    var isLogic = function (char) {
        return '&|'.indexOf(char) >= 0;
    };
    var isParen = function (char) {
        return '()'.indexOf(char) >= 0;
    };

    var Operations = {
        '+': function (l, r) { return l + r; },
        '-': function (l, r) { return l - r; },
        '*': function (l, r) { return l * r; },
        '/': function (l, r) { return l / r; },
        '%': function (l, r) { return l % r; },
        '>': function (l, r) { return l > r; },
        '>=': function (l, r) { return l >= r; },
        '<': function (l, r) { return l < r; },
        '<=': function (l, r) { return l <= r; },
        '==': function (l, r) { return l == r; },
        '!=': function (l, r) { return l != r; },
        '&&': function (l, r) { return l && r; },
        '||': function (l, r) { return l || r; }
    };

    var ASTNode = function (atom) {
        this.type = atom.type;
        this.value = atom.value;
        if (this.type === atomMap.NUMBER && typeof this.value === 'string') {
            this.value = this.value.indexOf('.') >= 0 ? parseFloat(this.value) : parseInt(this.value);
        }

        this.leftNode = null;
        this.rightNode = null;
        this.parentNode = null;
        this.complete = false;
        this.level = 1;
    };
    ASTNode.prototype.getValue = function (paramMap) {
        var value = 0;
        switch (this.type) {
            case atomMap.NUMBER:
                value = this.value;
                break;
            case atomMap.PARAM:
                if (typeof paramMap[this.value] === 'string') {
                    value = paramMap[this.value].indexOf('.') >= 0 ? parseFloat(paramMap[this.value]) : parseInt(paramMap[this.value]);
                }
                else {
                    value = paramMap[this.value];
                }
                break;
            case atomMap.OPERATION:
            case atomMap.COMPARE:
            case atomMap.LOGIC:
                value = Operations[this.value](this.leftNode.getValue(paramMap), this.rightNode.getValue(paramMap));
                break;
            default:
                break;
        }
        return value;
    };

    var createSubList = function (atomList) {
        var i = 0, temp, leftStack = [], lIndex;
        for (i = 0; i < atomList.length; i++) {
            temp = atomList[i];
            if (temp.type === atomMap.LPAREN) {
                leftStack.push(i);
            }
            if (temp.type === atomMap.RPAREN) {
                lIndex = leftStack.pop();
                sublist = atomList.splice(lIndex, i - lIndex + 1);
                sublist.pop(); sublist.shift();

                atomList.splice(lIndex, 0, { type: atomMap.SUBLIST, value: sublist });
                i = lIndex;
            }
        }

        return atomList;
    };
    var getAtomList = function (expression) {
        expression = expression.replace(/\s/gm, '').split('');

        var atomList = [];
        var i = 0, temp;
        var parenCount = 0, lastAtomType = null, err;
        var negative = false, negativeParenPos = 0;
        var isInCompare = false;

        for (i = 0; i < expression.length; i++) {
            temp = expression[i];
            if (isDigit(temp)) {
                if (lastAtomType === atomMap.PARAM ||
                    lastAtomType === atomMap.NUMBER ||
                    lastAtomType === atomMap.RPAREN) {
                    err = getErr(i); break;
                }

                while (isDigit(expression[i+1])) {
                    temp += expression[i+1];
                    i++;
                }
                atomList.push({ value: temp, type: atomMap.NUMBER });
                if (negative && negativeParenPos === false) { atomList.push({ value: ')', type: atomMap.RPAREN }); negative = false; }
                lastAtomType = atomMap.NUMBER;
            }
            else if (isParam(temp)) {
                if (lastAtomType === atomMap.PARAM ||
                    lastAtomType === atomMap.NUMBER ||
                    lastAtomType === atomMap.RPAREN) {
                    err = getErr(i); break;
                }

                atomList.push({ value: temp, type: atomMap.PARAM });
                if (negative && negativeParenPos === false) { atomList.push({ value: ')', type: atomMap.RPAREN }); negative = false; }
                lastAtomType = atomMap.PARAM;
            }
            else if (isOperat(temp)) {
                if (lastAtomType === null ||
                    lastAtomType === atomMap.OPERATION ||
                    lastAtomType === atomMap.LPAREN ||
                    lastAtomType === atomMap.COMPARE ||
                    lastAtomType === atomMap.LOGIC) {
                    if (temp === '-' && (isDigit(expression[i + 1]) || isParam(expression[i + 1]) || expression[i + 1] === '(')) {
                        atomList.push({ value: '(', type: atomMap.LPAREN });
                        atomList.push({ value: '0', type: atomMap.NUMBER });
                        negative = true;
                        if (expression[i + 1] === '(') negativeParenPos = parenCount;
                        else negativeParenPos = false;
                    }
                    else {
                        err = getErr(i); break;
                    }
                }
                atomList.push({ value: temp, type: atomMap.OPERATION });
                lastAtomType = atomMap.OPERATION;
            }
            else if (isParen(temp)) {
                if (temp === '(') {
                    if (lastAtomType === atomMap.NUMBER ||
                        lastAtomType === atomMap.PARAM ||
                        lastAtomType === atomMap.RPAREN) {
                        err = getErr(i); break;
                    }

                    atomList.push({ value: temp, type: atomMap.LPAREN });
                    parenCount++;
                    lastAtomType = atomMap.LPAREN;
                }
                else {
                    if (lastAtomType === null ||
                        lastAtomType === atomMap.OPERATION ||
                        lastAtomType === atomMap.LPAREN ||
                        lastAtomType === atomMap.COMPARE ||
                        lastAtomType === atomMap.LOGIC) {
                        err = getErr(i); break;
                    }

                    atomList.push({ value: temp, type: atomMap.RPAREN });
                    parenCount--;

                    if (negative && negativeParenPos === parenCount) { atomList.push({ value: ')', type: atomMap.RPAREN }); negative = false; }
                    lastAtomType = atomMap.RPAREN;
                }
            }
            else if (isCompare(temp)) {
                if (lastAtomType === null ||
                    lastAtomType === atomMap.LPAREN ||
                    lastAtomType === atomMap.OPERATION ||
                    lastAtomType === atomMap.COMPARE ||
                    lastAtomType === atomMap.LOGIC) {
                    err = getErr(i); break;
                }
                if (isInCompare) {
                    err = getErr(i); break;
                }

                if ((temp === '>' || temp === '<') && expression[i+1] === '=') {
                    i++; temp += '=';
                }
                else if (temp === '=' || temp === '!') {
                    if (expression[i+1] === '=') {
                        i++; temp += '=';
                    }
                    else {
                        err = getErr(i); break;
                    }
                }
                atomList.push({ value: temp, type: atomMap.COMPARE });
                lastAtomType = atomMap.COMPARE;
                isInCompare = true;
            }
            else if (isLogic(temp)) {
                if (lastAtomType === null ||
                    lastAtomType === atomMap.LPAREN ||
                    lastAtomType === atomMap.OPERATION ||
                    lastAtomType === atomMap.COMPARE ||
                    lastAtomType === atomMap.LOGIC) {
                    err = getErr(i); break;
                }

                if (expression[i+1] !== temp) {
                    err = getErr(i); break;
                }

                i++;
                temp = temp + temp;
                atomList.push({ value: temp, type: atomMap.LOGIC });
                lastAtomType = atomMap.LOGIC;
                isInCompare = false;
            }
            else {
                err = getErr(i); break;
            }
        }
        if (!err && parenCount !== 0) {
            err = { message: 'paren error', error: true };
        }
        if (!err && (atomList[atomList.length - 1].type === atomMap.OPERATION || atomList[atomList.length - 1].type === atomMap.LPAREN)) {
            err = getErr(i);
        }

        if (err) {
            throw err;
        }
        else {
            return atomList;//createSubList(atomList);
        }
    };
    var createAST_Old = function (atomList) {
        var i = 0, temp, tempNode;
        var lastNode, currentLevel;
        for (i = 0; i < atomList.length; i++) {
            temp = atomList[i];
            if (temp.type === atomMap.SUBLIST) { tempNode = createAST_Old(temp.value); }
            else { tempNode = new ASTNode(temp); }

            if (lastNode) {
                if (tempNode.type === atomMap.OPERATION && !tempNode.complete) {
                    if (lastNode.type === atomMap.OPERATION && !lastNode.complete
                        && (lastNode.value === '+' || lastNode.value === '-')
                        && (tempNode.value === '*' || tempNode.value === '/')) {
                        tempNode.leftNode = lastNode.rightNode;
                        lastNode.rightNode = tempNode;
                        tempNode.parentNode = lastNode;
                    }
                    else {
                        tempNode.leftNode = lastNode;
                        lastNode.parentNode = tempNode;
                    }
                    lastNode = tempNode;
                }
                else {
                    lastNode.rightNode = tempNode;
                    tempNode.parentNode = lastNode;
                }
            }
            else {
                lastNode = tempNode;
            }
        }

        while (lastNode.parentNode) { lastNode = lastNode.parentNode; }

        lastNode.complete = true;
        return lastNode;
    };
    var createAST = function (atomList) {
        var i = 0, temp, tempNode, tempLevel = 0;
        var lastNode, lastRightNode;
        for (i = 0; i < atomList.length; i++) {
            temp = atomList[i];
            if (temp.type === atomMap.SUBLIST) { tempNode = createAST(temp.value); }
            else { tempNode = new ASTNode(temp); }
            tempNode.level = tempNode.type;
            if (tempNode.type === atomMap.NUMBER || tempNode.type === atomMap.PARAM || tempNode.complete) {
                tempNode.level = 1;
            }
            else if (tempNode.type === atomMap.OPERATION && (tempNode.value === '+' || tempNode.value === '-')) {
                tempNode.level = tempNode.level + 5;
            }

            if (!lastNode) lastNode = tempNode;
            else {
                if (tempNode.level >= lastNode.level) {
                    lastNode.parentNode = tempNode;
                    tempNode.leftNode = lastNode;
                    lastNode = tempNode;
                }
                else {
                    lastRightNode = lastNode.rightNode;
                    if (lastRightNode) {
                        while (lastRightNode.rightNode && lastRightNode.level > tempNode.level) {
                            lastRightNode = lastRightNode.rightNode;
                        }
                        if (lastRightNode.level > tempNode.level) {
                            tempNode.parentNode = lastRightNode;
                            lastRightNode.rightNode = tempNode;
                        }
                        else {
                            tempNode.parentNode = lastRightNode.parentNode;
                            tempNode.parentNode.rightNode = tempNode;
                            tempNode.leftNode = lastRightNode;
                            lastRightNode.parentNode = tempNode;
                        }
                    }
                    else {
                        lastNode.rightNode = tempNode;
                        tempNode.parentNode = lastNode;
                    }
                }
            }
        }

        while (lastNode.parentNode) { lastNode = lastNode.parentNode; }
        lastNode.complete = true;
        return lastNode;
    };

    var parseExpression = function (expression) {
        var atoms = getAtomList(expression);
        atoms = createSubList(atoms);
        var ASTRoot = createAST(atoms);

        return ASTRoot;
    };
    var compute = function (expression, paramMap) {
        return parseExpression(expression).getValue(paramMap);
    };

    module.exports = {
        atomMap: atomMap,
        getAtomList: getAtomList,
        createSubList: createSubList,
        createAST: createAST,
        //createAST_Old: createAST_Old,
        parseExpression: parseExpression,
        compute: compute
    };

    //TODO use in test
    //window.CE = module.exports;
});

/*
var unitTest = function () {
    var tests = [
        { input: '(A+B)*C/D+(F-E)*2',
            param: { A: 6, B: 9.8, C: 11, D: 12, E: 5, F: 0.6 },
            output: (6+9.8)*11/12+(0.6-5)*2 },
        { input: '(3+11)*7/8.4+6.75+(7-9)*2.50', output: (3+11)*7/8.4+6.75+(7-9)*2.50 },
        { input: '-6*5', output: -6*5},
        { input: '-6*-8', output: -6*-8 },
        { input: '9*-(6*3)', output: 9*-(6*3) }, //4
        { input: '(2A+B)', output: 'err' },
        { input: '(A++B)', output: 'err' },
        { input: '((A+B)', output: 'err' },
        { input: '(A+B)B-6', output: 'err' },
        { input: 'A+BC-6', output: 'err' },//9
        { input: '-6-', output: 'err' },
        { input: '6 > 5', output: true },
        { input: '7 + 8 > 12', output: true },
        { input: '5 > 6 || 8 > 7', output: true },
        { input: '5 > 6 && 8 > 7', output: false },//14
        { input: '5 > (2 + 1)', output: true },
        { input: '5 > 4 > 3', output: 'err' },
        { input: '> 4 + 3', output: 'err' },
        { input: '6 => 4', output: 'err' },
        { input: '6 + (> 4)', output: 'err' },//19
        { input: '6 + > 4', output: 'err' },
        { input: '5 += 4', output: 'err' }
    ];

    tests.forEach(function (x) {
        var result;
        try { result = CE.compute(x.input, x.param); }
        catch (e) { result = e; }
        if (typeof result === 'object' && result.error) {
            console.log(x.output === 'err');
        }
        else {
            console.log(result == x.output);
        }
    });
};
var printAtomlist = function (list) {
    console.log(list.map(function (x) { return x.value; }).join(','));
};
*/
