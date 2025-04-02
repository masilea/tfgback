package main.java.es.upm.dit.isst.tfgapi.repository;

public interface TFGRepository extends CrudRepository<TFG, String> {
    List<TFG> findByTutor(String tutor);
}

