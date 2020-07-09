# The movie database Data

## Descripcion
La libreria permite obtener los datos de una pelicula mediante la fuente The Movie Database. Provee una descripción de la pelicula y un enlace al poster de la misma.

## Uso de la librería

## Como iniciar el servicio externo

Este módulo provee una interfaz TMDBService para obtencion de peliculas que debe ser obtenido a través del objeto TMDBDataModule de la siguiente manera:

TMDBDataModule.tmdbService

### Como obtener una pelicula

La pelicula se obtiene mediante la invocación de la función getMovie de la clase TMDBService de la siguiente manera:

getMovie(String title, String year): TMDBMovie

Los datos de la pelicula son devuelvos en un objeto de tipo TMDBMovie que tiene las propiedades:
  
  title:String
  
  plot:String
  
  imageUrl:String
  
  posterPath:String

Si la pelicula no es encontrada, se devuelve un objeto TMDBMovie inicializado por defecto
