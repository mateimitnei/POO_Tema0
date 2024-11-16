## GwentStone Lite
### Autor: Matei Mițnei

---

### Structura

- Pachetul `org.poo.game` conține clasele principale ale jocului:

1. **GameEngine** (`src/main/java/org/poo/game/GameEngine.java`):
   - Gestionează fluxul jocului folosind clasa `Input` pentru datele de intrare  
și clasa `Table` pentru plasarea și acțiunile cărților. Tot aici se prelucreaza  
și outputul pentru a fi afișat în format json.

2. **Player** (`src/main/java/org/poo/game/Player.java`):
   - Gestionează eroul, mâna de cărți, deck-ul și mana jucătorului.

3. **Table** (`src/main/java/org/poo/game/Table.java`):
   - Conține cărțile de pe tabla si metode pentru plasarea cărților, atacuri și  
utilizarea abilităților.

4. **Card** (`src/main/java/org/poo/game/Card.java`):
   - Stochează starea cărții (înghețată, atacată), pe lângă informatiile din input.

- Pachetele `org.poo.game.heroes` și `org.poo.game.minions` conțin clasele pentru  
fiecare tip de card. Acestea extind clasa `Card` și implementează metoda `ability`  
specifică fiecăreia.

### Fluxul programului

1. **Inițializarea jocului**:
   - `GameEngine` este inițializat cu datele de intrare (`Input`).
   - Metoda `start` din `GameEngine` initializeaza jocul cu datele curente  
(`GameInput`), setând eroii și pachetele de cărți ale jucătorilor și pregătind  
masa de joc (`Table`).

2. **Executarea jocului**:
   - Metoda `play` din `GameEngine` procesează acțiunile (`ActionsInput`) și le  
aplică pe masa de joc (`Table`).
   - Unele acțiuni 

3. **Gestionarea acțiunilor**:
   - `Table` se ocupă de plasarea cărților pe rândurile corespunzătoare și  
executarea atacurilor și abilităților.
   - `Player` modifica mana, mâna și deck-ul de cărți și eroul jucătorului.
   - `Card` și `Hero` reprezintă entități de joc care pot să efectueze acțiuni.

### Provocări și soluții

- **Polimorfism**: Inițial am implementat folosirea abilitatilor doar in clasa  
`Table`, cu o structura imbârlighata de if-uri (în funcție de numele carții). Am  
refăcut, folosindu-mă de clasele din pachetul `minions` si `heroes` care fac  
override la metoda `ability` din `Card`.

---

#### Feedback

în general mulțumit de temă, dar mi se pare baremul puțin prea exigent, având în  
vedere că ni s-a zis că tema se poate rezolva și doar pe baza cunoștințelor de  
la primele trei laboratoare.
