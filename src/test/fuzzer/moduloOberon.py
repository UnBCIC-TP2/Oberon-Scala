from ntpath import join
import random, string
import expAritmeticas
import grammar as G

def newModule():
    name = ""
    for i in range(8):
        name += join(random.choice(string.ascii_letters + string.digits))

    types = ["SHORTINT", "LONGINT"]

    arquivo = open(f'modulos\{name}.oberon', "w")
    arquivo.write(f'Module {name};\n\n')

    arquivo.write('VAR\n')
    
    nVar = random.randint(1,10)
    Vars = []
    for _ in range(nVar):
        var = random.choice(string.ascii_uppercase)
        if var not in Vars:
            Vars.append(var)
            arquivo.write(f' {var} : {random.choice(types)};\n')

    G.grammar["<variable>"] = ["1"]

    arquivo.write('\nBEGIN\n')
    for x in Vars[:len(Vars)-1]:
        arquivo.write(f' {x} := {G.grammar_fuzzer()};\n')
        G.grammar["<variable>"].append(x)  #Add apenas as variáveis já declaradas no grammar
    arquivo.write(f' {Vars[-1]} := {G.grammar_fuzzer()}\n')
    arquivo.write(f'\nEND\n\nEND {name}.')
    arquivo.close()


for _ in range(5):
    newModule()