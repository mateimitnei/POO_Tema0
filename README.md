## TEMA 0 - GwentStone Lite
### Autor: Matei Mițnei

---

### Componentele soluției

1. **GameEngine** (`game/GameEngine.java`):
    - Utilizează clasa `Input` pentru a obține datele de intrare și clasa `Table` pentru a gestiona plasarea și acțiunile cărților pe masă.

2. **Player** (`game/Player.java`):
    - Gestionează eroul, mâna de cărți, pachetul de cărți și mana jucătorului.

3. **Table** (`game/Table.java`):
    - Are metode pentru plasarea cărților, atacuri și utilizarea abilităților.

4. **Card** (`game/Card.java`):
    - Conține informații precum nume, descriere, culori, viață, atac, mana și starea cărții (înghețată, daca a atacat sau nu).

### Flow-ul dintre componente

1. **Inițializarea jocului**:
    - `GameEngine` este inițializat cu datele de intrare (`Input`).
    - Metoda `start` din `GameEngine` inițializează jocul cu datele curente (`GameInput`), setând eroii și pachetele de cărți ale jucătorilor și pregătind masa de joc (`Table`).

2. **Desfășurarea jocului**:
    - Metoda `play` din `GameEngine` parcurge acțiunile (`ActionsInput`) și le aplică pe masa de joc (`Table`).
    - Acțiunile includ plasarea cărților, atacuri, utilizarea abilităților și comenzi de debug si statistici.

3. **Gestionarea acțiunilor**:
    - `Table` gestionează plasarea cărților pe rândurile corespunzătoare și aplicarea atacurilor și abilităților.
    - `Player` gestionează mana, mâna de cărți și eroul jucătorului.
    - `Card` și `Hero` reprezintă entitățile care pot fi plasate pe masă și care pot efectua acțiuni.
