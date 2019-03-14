file = open('F:\\Docs\\Sem2\\ABDA\\gr0.California_links.txt', 'r')
lines = file.readlines()
lines = [x.strip() for x in lines]
file.close()
edges = [x[2:] for x in lines if x[0] == 'e']
nodes = [x[2:] for x in lines if x[0] == 'n']

vertices = {}

for n in nodes:
    id,val = n.split()
    if val[-1] == '/':
        val = val[:-1]
    vertices[id] = val

result = {}
for e in edges:
    src,dst = e.split()
    src = vertices[src]
    dst = vertices[dst]
    if not src in result:
        result[src] = set()
    result[src].add(dst)

for val in vertices.values():
    if val not in result:
        result[val] = set()

out = open('F:\\Docs\\Sem2\\ABDA\\input.txt', 'w')

for src in result.keys():
    ns = result[src]
    text = src + '\t'
    for e in ns:
        text += e + '\t'
    text = text[:-1]
    text += '\n'
    out.write(text)