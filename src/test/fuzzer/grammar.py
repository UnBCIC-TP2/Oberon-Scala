import re
import random

grammar = {
    "<start>" : ["<expr>"],

    "<expr>" : ["<term> + <expr>", "<term> - <expr>", "<term>"],

    "<term>" : ["<factor> * <term>", "<factor> / <term>", "<factor>"],

    "<factor>" : ["<factor>", "<factor>", "(<expr>)", "<integer>.<integer>", "<integer>", "<variable>"],

    "<integer>" : ["<digit><integer>", "<digit>"],

    "<digit>" : ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9"],

    "<variable>" : ["1"]
}

Regex_Nonterminal = re.compile(r'(<[^<> ]*>)')  #Cria objeto RegEx do tipo '<...>'

def nonterminals(expansion):                    #Pega uma string e separa em nonterminals
    #if isinstance(expansion, tuple):
    #    expansion = expansion[0]
    return Regex_Nonterminal.findall(expansion)

def is_nonterminal(s):
    return Regex_Nonterminal.match(s)

class ExpansionError(Exception):
    pass

START_SYMBOL = "<start>"

def grammar_fuzzer(Grammar = grammar, start_symbol = START_SYMBOL, max_nonterminals  = 10, max_expansion_trials = 100, log = False) -> str:
    term = start_symbol     
    expansion_trials = 0

    while len(nonterminals(term)) > 0:          #Roda enquanto existir termos do tipo nonterminal
        symbol_to_expand = random.choice(nonterminals(term))
        expansions = Grammar[symbol_to_expand]
        expansion = random.choice(expansions)

        #if isinstance(expansion, tuple):
        #    expansion = expansion[0]

        new_term = term.replace(symbol_to_expand, expansion, 1)
        
        if len(nonterminals(new_term)) < max_nonterminals:
            term = new_term
            #if log:
            #    print("%-40s" % (symbol_to_expand + " -> " + expansion), term)
            expansion_trials = 0

        else:       #Impede expansões muito longas
            expansion_trials += 1
            if expansion_trials >= max_expansion_trials:
                raise ExpansionError("Cannot expand " + repr(term))

    return term


