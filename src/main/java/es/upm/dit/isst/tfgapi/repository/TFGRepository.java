package es.upm.dit.isst.tfgapi.repository;
import org.springframework.data.repository.CrudRepository;
import es.upm.dit.isst.tfgapi.model.TFG;
import java.util.List;


public interface TFGRepository extends CrudRepository<TFG, String> {
    List<TFG> findByTutor(String tutor);
}

