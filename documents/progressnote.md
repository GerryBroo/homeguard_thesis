# Haladási napló

* 2022.09.13.
    * Felkészítettem a projektet a félévre
    * Csináltam egy release-t, hogy lássom mi volt az előző féléves munkám
    * Refactor-ral foglalkoztam, a camerán van még mit dolgozni
    * Elkezdtem elkészíteni a home screen-t

* 2022.09.18.
    * Kiszedtem a camera preview-ből az add new face opciót
    * Létrehoztam egy új activity-t, ami képes az arcok érzékelésére és az új arcok felvételére
    * Az új arc hozzáadásához feltételeket írtam, 2szer úgyanazt az arcot nem lehet felvenni, ha nincs arc akkor sem lehet felvenni. Csak ha a kijelzön UNKNOW szerepel

* 2022.09.19.
    * Létrehoztam a history elementek számára az adatbázist, illetve egy recycleview a history fragmentben
    * kitaláltam az arc mentési logikát:
        * Ha 15 mp-en belül leglaább 3 mp keresztül érzékeli úgyanazt az arcot akkor elmenti, mentés után 2 percig nem menti el úgyanazt az arcot

* 2022.09.20.
    * Arc mentési logika implementálása, működik újra indítani a számlálot még nem tudja, és nagyon csúnya a kód nekem nem tetszik, TODO átdolgozni

* 2022.09.22.
    * Arc mentés logika működik rendeltetés szerűen, TODO kód átdolgozás szebbé
    * History befejézse

* 2022.09.25.
    * Refactor: ViewModel beépítése a CameraPreview-ba

* 2022.09.27.
    * Desgin átalakítása, egységesítése
    * Object detection beépítése

* 2022.09.30.
    * Kukásautó model elkészítésének elkezdése: Képek gyűjtöttem az internetről és elkészítettem egy CVS Dastaset-et, azonban nem sikerült működésre bírjam a python scriptet, ami egy tflite modelt készítene. Mindig megakad egy pluginon, amit általában nem tudok feltelepíteni

* 2022.10.05.
    * Befejezte a multidetector-t teszteltem az első félévben elkészített madáretető madárfelismerő model-el
    * tovább folytattam a kukásautó model készítését, de ismétetlen nem sikerült
    * létrehoztam a beadandó dokumentumot és elkezdtem megtevezni és megírni a vázát