import random

def geraExp():
    expAritmetica = ""
    parenteses = False
    op = "+-*/"

    probParenteses = 0.3
    for i in range(2):

        if random.random() < probParenteses:
            parenteses = True

        exp = ""
        qtdN = random.randint(1,5)
        for qtd in range(qtdN):
            exp += f'{random.randint(0,1000)}'
            exp += f'{random.choice(op)}'

        
        if parenteses and i != 1:
            expAritmetica += f'({exp[:-1]}){random.choice(op)}'
        expAritmetica += exp[:-1]
        
    return expAritmetica

#for i in range(5):
#    print(geraExp())





