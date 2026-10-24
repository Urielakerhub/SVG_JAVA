# Cesar Uriel Aguiar Altamirano.
# Uriel Santos.
Expedientes
a225204109
a225204882
# Reporte técnico: Procesamiento y representación de documentos SVG en Java

## 1. ¿Qué ventajas ofrece utilizar SAX para procesar documentos SVG?

Al trabajar en este proyecto, comprendimos que SAX es una herramienta útil para procesar documentos SVG porque permite leer sus etiquetas y atributos de manera secuencial, sin necesidad de cargar todo el documento en memoria al mismo tiempo.
En nuestra implementación, utilizamos la clase `TagsHandler`, que procesa los eventos mediante métodos como `startElement()`, `endElement()` y `characters()`. De esta manera, identificamos los elementos gráficos y recuperamos sus atributos para almacenarlos y utilizarlos posteriormente en el dibujo.
Una de las ventajas que encontramos es que podemos separar la lectura del documento de su representación visual. Esto facilita la organización del código y permite identificar con mayor claridad dónde se procesa la información y dónde se utiliza. También consideramos que SAX resulta conveniente para este tipo de proyecto porque nos permite trabajar directamente con los eventos generados durante la lectura del archivo XML.

## 2. ¿Qué diferencias existen entre interpretar la información geométrica de un elemento y representarlo gráficamente?

Durante el desarrollo, distinguimos dos procesos fundamentales: interpretar los datos de una figura y convertir esos datos en una representación visible.
Interpretar consiste en recuperar información como coordenadas, dimensiones, radios y colores. Por ejemplo, para un círculo obtenemos los atributos `cx`, `cy` y `r`. En cambio, representarlo gráficamente significa utilizar esos valores para construir y dibujar la figura mediante las herramientas gráficas de Java.
En nuestro código, `TagsHandler` se encarga de procesar la información del documento, mientras que `SVGDiagram` utiliza los datos almacenados para dibujar los elementos. También contamos con la clase `Geometric`, que permite realizar cálculos como áreas, perímetros y longitudes.
A partir de esta separación, comprendimos que leer correctamente una figura no significa necesariamente que vaya a visualizarse de forma correcta. Para conseguirlo, también necesitamos implementar las operaciones de dibujo correspondientes y asegurarnos de que los datos interpretados se utilicen adecuadamente.

## 3. ¿Cómo contribuye el polimorfismo a simplificar la representación de distintos elementos gráficos?

Al revisar las clases del proyecto, observamos que cada tipo de figura necesita una forma particular de representarse. Por ejemplo, `Rect` trabaja con rectángulos, `Ellipse` con elipses, `Polyline` con líneas conectadas y `Text` con contenido textual.
Estas clases cuentan con métodos `draw()` que contienen las instrucciones específicas para dibujar cada elemento. Esto nos permitió comprender cómo se puede organizar la lógica gráfica de acuerdo con las características de cada figura.
El polimorfismo permitiría simplificar todavía más esta estructura, ya que podríamos definir una operación de dibujo común y hacer que cada figura la implemente a su manera. Así, el programa podría recorrer una colección de figuras y solicitar que cada una se dibuje sin comprobar manualmente su tipo.
Sin embargo, identificamos que `SVGDiagram` todavía utiliza condiciones `if` y `else if` para distinguir los elementos mediante su atributo `type`. Por ello, consideramos que una mejora sería integrar una interfaz común para las figuras y aprovechar directamente el polimorfismo. Esto haría que el código fuera más sencillo de ampliar y mantener.

## 4. ¿Por qué es importante conservar el orden de los elementos dentro de un documento SVG?

Comprendimos que el orden de los elementos influye directamente en el resultado visual, especialmente cuando varias figuras se superponen. En SVG, los elementos que aparecen después normalmente se dibujan encima de los anteriores.
En nuestro proyecto, `TagsHandler` almacena las figuras en una lista `ArrayList` conforme se procesan las etiquetas. Posteriormente, `SVGDiagram` recorre esa lista para dibujarlas. De esta manera, se conserva la secuencia en la que fueron encontradas durante la lectura del documento.
Por ejemplo, si dibujamos primero un rectángulo y después un círculo que se superpone parcialmente, el círculo puede cubrir una parte del rectángulo. Si alteramos el orden, el resultado podría cambiar.
Por esta razón, consideramos que mantener el orden original es necesario para respetar la composición del archivo SVG y evitar diferencias entre el documento descrito y la imagen que genera nuestro programa.

## 5. ¿Qué modificaciones serían necesarias para incorporar nuevos elementos como `circle`, `polygon` o `path`?

Para ampliar nuestro visualizador, identificamos que sería necesario trabajar tanto en la interpretación de los atributos como en la representación gráfica de cada elemento.
En el caso de `circle`, nuestro código ya contempla su procesamiento en `TagsHandler` y su representación en `SVGDiagram`, utilizando los atributos `cx`, `cy` y `r`, junto con propiedades de estilo como `fill`, `stroke` y `stroke-width`.
Para incorporar `polygon`, tendríamos que procesar el atributo `points`, separar las coordenadas de sus vértices y conectarlos para construir una figura cerrada. Para dibujarlo podríamos utilizar herramientas de Java como `Polygon` o `Path2D`.
En el caso de `path`, la implementación sería más compleja, porque el atributo `d` puede contener instrucciones para crear segmentos rectos, curvas y otras formas. Sería necesario interpretar esas instrucciones y convertirlas en una estructura gráfica que Java pudiera representar.
Finalmente, tendríamos que integrar los nuevos elementos en el procesamiento SAX y en la lógica de dibujo, además de realizar pruebas para verificar que sus coordenadas, colores, contornos y orden de representación se procesen correctamente.
Este análisis nos permitió identificar que reconocer una etiqueta es solamente una parte del trabajo: también debemos comprender sus atributos y definir cómo se transformará en una figura visible.

## Conclusión

El desarrollo de este proyecto nos permitió comprender mejor cómo se relacionan el procesamiento de documentos XML y la representación gráfica en Java. Mediante SAX, podemos leer las etiquetas y recuperar sus atributos; posteriormente, utilizamos esa información para construir y dibujar los elementos del documento SVG.
También identificamos la importancia de conservar el orden de las figuras y de organizar el
