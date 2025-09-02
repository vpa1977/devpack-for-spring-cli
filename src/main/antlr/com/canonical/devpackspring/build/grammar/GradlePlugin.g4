grammar GradlePlugin;

sequence : ANY PLUGIN LBRACE ANY RBRACE ANY EOF;

PLUGIN     : 'plugin';
LBRACE     : '{';
RBRACE     : '}';
WHITESPACE : [ \t\r\n]+ -> skip;
PLUGIN_BLOCK : . ;
ANY        : . ;