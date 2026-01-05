# 🖼️  Kernel Image Processing (Sekvenčna izvedba)

Kernel image processing je temeljna tehnika računalniškega vida, kjer sliko obdelamo tako, da čez njo “drsi” majhen filter (kernel) in na vsakem pikslu izračuna novo vrednost na podlagi pikslov ki so okoli njega - v njegovi okolici. To je osnova za ogromno realnih funkcij: zamegljevanje - blur(odstranjevanje šuma), ostrenje - sharpen (poudarjanje detajlov), zaznavanje robov (npr. Sobel/edge detection), izboljšanje kontrasta in pripravo slike za nadaljnjo analizo. Ker so kerneli hitri, predvidljivi in dobro delujejo na različnih tipih slik, se uporabljajo praktično povsod — od kamer na telefonih in Instagram/CapCut filtrov, do medicinskega slikanja, industrijske kontrole kakovosti, OCR/scan izboljšav, pa tudi kot “prvi korak” v pipeline-u za bolj napredne metode, kot so modeli za prepoznavanje objektov in segmentacijo.

## 🧩 Kaj program dela?
Mi kot uporabnik programa damo programu eno ali več slik svojih poljubnih slik (lahko izbiramo tudi med slikami, ki so prednaložene že v programu). Nato izberemo katero oziroma katere operacije želimo da se izvedejo na vsaki od izbranih slik. Lahko izberemo eno operacijo lahko jih izberemo več. In potem program na vsaki od teh slik izvede izbrane operacije.

## 🧪 Primeri uporabe (Use Case)

### 1. Primer uporabe
- Izberemo sliko `2048x2048-Slika.jpg`. 
- Izberemo operacije blur in mirror. (v konzoli se nam izpiše vrstni red operacij) - v tem vrstnem redu se bodo izvedle. 
- Kliknemo gumb `Obdelaj izbrano sliko` 
- V mapi `ustvarjene slike` se nam pojavi rezultat

### 2. Primer uporabe
- Izberemo sliko `2048x2048-Slika.jpg`
- Izberemo operacije; blur, edge detection in sharpen (v konzoli se nam izpiše vrstni red operacij) - v tem vrstnem redu se bodo izvedle. 
- Kliknemo gumb `Obdelaj izbrano sliko` 
- V mapi `ustvarjene slike` se nam pojavi rezultat

### 3. Primer uporabe
- Izberemo operacije; blur, edge detection in sharpen (v konzoli se nam izpiše vrstni red operacij) - v tem vrstnem redu se bodo izvedle. 
- Kliknemo gumb `Obdelaj mapo slik` in izberemo mapo v kateri so neke slike
- Izberemo to mapo in 
- V mapi `ustvarjene slike` se nam pojavi rezultat (za vsako od teh slik se je naredila sekvenca izbranih operacij)



## 🚩 Navodila za zagon programa

1. Če programa še nimaš lokalno ga namestiš s komando:
` git clone https://github.com/Zankooo/Kernel-Image-Sequential.git `
2. Program zaženeš tako da zaženeš Main.java in mora delovati. Pri implementaciji sem uporabljal `open jdk-24.0.2` vendar bi program moral delovati tudi na drugih verzijah Jave. 

## 📝 Opombe
- V celotnem `README.md` ne omenjam da izvedemo konvolucije ampak operacije. To pa zato ker blur, edge detection... že res so konvolucije ampak mirror ne moremo šteti kot konvolucijo ampak je bolj transformacija. 
- Če izberemo tudi operacijo Mirror se bo Mirror operacija vedno zadnja izvedla! Sekvenca operacij (ena za drugo v izbranem vrstnem redu) šteje le za konvolucije. Medtem ko se, če izberemo mirror, zvede vedno zadnja. 


## 🏁 Testiranje
Testiranje sem opravil na svojem osebnem računalniku:
MacBook Pro M1 Max 64Gb/2Tb. 

Pri vseh treh verzijah programa (sekvenčni, vzporedni in porazdeljeni) sem (bom) opravil testiranje na popolnoma istih slikah na popolnoma identičnih operacijah. 

### Testing Table - to še naredit

|    Slika | n = 2     | n = 3     | n = 4     | n = 5     |
|-----------|-----------|-----------|-----------|-----------|
| **123MB** | 7.68 sec  | 11.41 sec | 14.74 sec | 14.81 sec |
| **234MB** | 21.20 sec | 29.46 sec | 34.22 sec | 37.31 sec |
| **350MB** | 32.41 sec | 48.56 sec | 51.07 sec | 54.04 sec |
| **490MB** | 33.26 sec | 42.82 sec | 53.34 sec | 60.85 sec |
| **613MB** | 35.01 sec | 53.12 sec | 64.67 sec | 74.80 sec |



## ⚡ Izboljšane oziroma drugačne verzije programa

Ta program je implementiran sekvenčno. 
Glede na njegovo strukturo nam daje možnost da ga optimiziramo. 
Optimizirani verziji `vzporedna (paralelna)` in `porazdeljena (distributed)` bosta na voljo kmalu... Coming soon


