DOKUMENTACE

  ------------------- ------------------ ---------------------------------
  **Třída**           **Vrstva**         **Účel**

  Geometry            1 --- základ       Body a bounding box, detekce
                                         kliknutí

  Shape               2 --- grafika      Obrys (Outline) a výplň (Fill)

  LineBasedShape      3 --- úsečky       Obrys spojující body úsečkami,
                                         ray casting

  Line, Circle,       4 --- tvary        Konkrétní tvary s vlastní logikou
  Rectangle,                             
  Triangle, Polygon                      
  ------------------- ------------------ ---------------------------------

Geometry -- abstraktní základ

Abstraktní třída Geometry je základem pro všechny tvary. Uchovává seznam
bodů (points) a ohraničující obdélník (bounding box). Ten slouží dvěma
účelům: při vytváření tvarů (s výjimkou polygonu) a při detekci kliknutí
uživatele.

Klíčové metody

-   BBoxContains(Point p) -- rychlá, ale přibližná kontrola, zda bod
    leží uvnitř bounding boxu. Používá se jako první, levný filtr před
    přesnější detekcí.

-   contains(Point p, int offset) -- abstraktní metoda, kterou si každý
    tvar implementuje sám. Je výpočetně náročnější, ale přesná. Parametr
    offset rozšiřuje oblast detekce směrem ven --- používá se při výběru
    kontrolních bodů myší, kdy chceme umožnit kliknutí v blízkosti bodu,
    ne přesně na něj.

-   GetNearestPointIndex(Point p) -- vrátí index bodu z points, který je
    nejblíže zadanému bodu (typicky pozici kurzoru). Slouží k určení,
    který kontrolní bod uživatel chce vybrat.

-   MovePoint(int index, Point newPos) -- přesune konkrétní kontrolní
    bod na novou pozici.

-   MoveBy(Point delta) -- posune celé těleso tak, že střed bounding
    boxu přesune na nové místo.

-   Recalculate() -- po každé změně bodů přepočítá bounding box. Každý
    tvar si tuto metodu implementuje sám.

BoundingBox -- ohraničující obdélník

BoundingBox je neměnný záznam (record) reprezentující osově zarovnaný
ohraničující obdélník definovaný dvěma rohovými body.

> public record BoundingBox(Point leftTop, Point rightBottom)

Konstruktor automaticky seřadí souřadnice, takže nezáleží na pořadí
předaných bodů --- leftTop bude vždy levý horní roh a rightBottom pravý
dolní roh.

  ------------------------ ----------------------------------------------
  **Metoda**               **Popis**

  width()                  Šířka boxu v pixelech

  height()                 Výška boxu v pixelech

  center()                 Střed boxu jako Point

  containsPoint(Point p)   Vrací true, pokud bod leží uvnitř nebo na
                           hraně boxu
  ------------------------ ----------------------------------------------

Shape -- grafická vrstva

Shape rozšiřuje Geometry o grafické vlastnosti: Outline (barva a
tloušťka obrysu) a Fill (barva výplně a příznak, zda je výplň aktivní).

-   drawOutline(Canvas canvas) -- abstraktní metoda; každý tvar si
    vykreslování obrysu implementuje sám, protože každý tvar se kreslí
    jinak.

-   fillGeometry(Canvas canvas) -- společná implementace výplně pro
    všechny tvary kromě Circle. Prochází pixel po pixelu celý bounding
    box a každý bod, pro který contains() vrátí true, zabarví barvou
    výplně. Circle tuto metodu přepisuje vlastní efektivnější
    implementací.

LineBasedShape -- tvar spojující body úsečkami

LineBasedShape rozšiřuje Shape a implementuje výchozí chování pro tvary,
jejichž obrys tvoří úsečky mezi sousedními body.

Implementace drawOutline

> protected void drawOutline(Canvas canvas) {
>
> for (int i = 0; i \< points.size(); i++) {
>
> Point a = points.get(i);
>
> Point b = points.get((i + 1) % points.size());
>
> LineDrawer.drawLine(canvas, a, b, outline);
>
> }
>
> }

Iteruje přes všechny body a vždy propojí aktuální bod s následujícím.
Operátor % points.size() zajistí, že poslední bod se spojí zpět s prvním
--- tvar se tak uzavře.

Implementace contains -- algoritmus ray casting

> public boolean Contains(Point point, int offset) {
>
> int n = points.size();
>
> boolean inside = false;
>
> int x = point.x(), y = point.y();
>
> for (int i = 0, j = n - 1; i \< n; j = i++) {
>
> int xi = points.get(i).x(), yi = points.get(i).y();
>
> int xj = points.get(j).x(), yj = points.get(j).y();
>
> boolean intersects = ((yi \> y) != (yj \> y)) &&
>
> (x \< (xj - xi) \* (y - yi) / (double)(yj - yi) + xi);
>
> if (intersects) inside = !inside;
>
> }
>
> if (!inside && offset \> 0) {
>
> for (int i = 0, j = n - 1; i \< n; j = i++) {
>
> if (distanceToSegment(point, points.get(j), points.get(i)) \<= offset)
>
> return true;
>
> }
>
> }
>
> return inside;
>
> }

Metoda pracuje ve dvou krocích:

-   **Ray casting** -- z testovaného bodu se myšleně vyšle paprsek
    vodorovně doprava. Počítá se, kolikrát paprsek překříží hranu
    polygonu. Lichý počet průsečíků = bod je uvnitř, sudý = bod je vně.
    Jde o standardní algoritmus funkční pro libovolný konvexní i
    konkávní polygon.

-   **Offset -- detekce blízkosti hrany** -- pokud bod není uvnitř, ale
    offset \> 0, zkontroluje se ještě, zda bod neleží maximálně offset
    pixelů od některé hrany. Vzdálenost počítá pomocná metoda
    distanceToSegment.

distanceToSegment(Point p, Point a, Point b) vypočítá kolmou vzdálenost
bodu p od úsečky ab. Promítne bod na přímku a výsledek ořízne na
interval \[0, 1\], takže výsledek nikdy nevychází mimo úsečku.

Jednotlivé tvary

Line

Jednoduchá úsečka definovaná dvěma body. Nemá výplň. Metoda contains
nekontroluje plochu (ta je nulová), ale přímo vzdálenost od úsečky. Práh
pro detekci je maximum z offset a tloušťky čáry outline.weight() ---
silnější čára je tak snáze kliknutelná.

Circle

Elipsa (nebo kružnice) definovaná třemi body. Třída se jmenuje Circle,
ale interně reprezentuje obecnou elipsu --- kružnice je speciální
případ, kdy jsou oba poloměry stejné.

  ------------- ------------- -------------------------------------------
  **Index**     **Bod**       **Popis**

  points\[0\]   střed         Střed elipsy

  points\[1\]   pravý bod     Poloměr v ose X: rx = \|points\[1\].x −
                              center.x\|

  points\[2\]   dolní bod     Poloměr v ose Y: ry = \|points\[2\].y −
                              center.y\|
  ------------- ------------- -------------------------------------------

Při bboxToCircle se oba poloměry nastaví na min(width, height) / 2, čímž
vznikne kružnice. Při bboxToEllipse se poloměry nastaví nezávisle podle
šířky a výšky boxu.

Metoda contains používá rovnici elipsy:

> (dx² / rx²) + (dy² / ry²) ≤ 1

kde dx a dy jsou vzdálenosti testovaného bodu od středu. Poloměry jsou
zvětšeny o offset.

Rectangle

Obdélník definovaný čtyřmi body (levý horní, pravý horní, pravý dolní,
levý dolní). Dědí drawOutline i contains z LineBasedShape. Metoda
bboxToSquare ořízne bounding box na čtverec výběrem kratší strany
(min(width, height)) a zarovná jej na levý horní roh.

Triangle

Trojúhelník definovaný třemi body. Přepisuje contains vlastní
implementací pomocí znaménkové metody:

> public boolean Contains(Point point, int OFFSET) {
>
> if (!boundingBox.containsPoint(point)) return false;
>
> Point a = points.get(0), b = points.get(1), c = points.get(2);
>
> float d1 = sign(point, a, b);
>
> float d2 = sign(point, b, c);
>
> float d3 = sign(point, c, a);
>
> boolean hasNeg = (d1 \< -OFFSET) \|\| (d2 \< -OFFSET) \|\| (d3 \<
> -OFFSET);
>
> boolean hasPos = (d1 \> OFFSET) \|\| (d2 \> OFFSET) \|\| (d3 \>
> OFFSET);
>
> return !(hasNeg && hasPos);
>
> }

Pomocná funkce sign(p, a, b) vypočítá, na které straně přímky ab se bod
p nachází (kladná = vlevo, záporná = vpravo). Bod je uvnitř trojúhelníku
tehdy, pokud leží na stejné straně u všech tří hran --- tzn. hodnoty d1,
d2, d3 mají všechny stejné znaménko. Parametr OFFSET toleruje hodnoty
těsně za hranou, čímž detekční oblast mírně rozšiřuje.

Před výpočtem proběhne rychlá kontrola bounding boxem (containsPoint),
která vyloučí většinu bodů bez nákladnějšího výpočtu.

Polygon

Libovolný polygon s proměnným počtem bodů. Plně dědí drawOutline i
contains z LineBasedShape. Body se zadávají přímo --- třída nepotřebuje
bounding box pro inicializaci.

BboxToShapeMaker -- továrna tvarů

Statická třída sloužící k vytváření tvarů z bounding boxu. Místo
zadávání konkrétních souřadnic stačí definovat ohraničující obdélník a
zvolit typ tvaru.

  --------------------------- ----------------------------------------------
  **Metoda**                  **Výsledek**

  bboxToRect                  Obdélník přesně kopírující bounding box

  bboxToSquare                Čtverec se stranou min(width, height),
                              zarovnaný na levý horní roh

  bboxToTriangle              Rovnoramenný trojúhelník; vrchol ve středu
                              horní hrany, základna na spodní hraně

  bboxToRightAngledTriangle   Pravoúhlý trojúhelník; pravý úhel v levém
                              horním rohu

  bboxToEllipse               Elipsa vyplňující celý bounding box

  bboxToCircle                Kružnice s poloměrem min(width, height) / 2,
                              vystředěná v boxu
  --------------------------- ----------------------------------------------
