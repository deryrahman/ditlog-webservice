package id.ac.itb.logistik.ditlog.repository;

import id.ac.itb.logistik.ditlog.model.Milestone;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.data.repository.PagingAndSortingRepository;


import javax.transaction.Transactional;

@Repository
public interface MilestoneRepository extends PagingAndSortingRepository<Milestone, Long> {

    @Query(value = "SELECT * FROM PROGRES_JASA WHERE ID_PROGRES = ?1", nativeQuery = true)
    Milestone findById(Long idProgres);

    @Query(value = "SELECT * FROM PROGRES_JASA WHERE ID_USER = ?1", nativeQuery = true)
    Iterable<Milestone> findByIdUser(Long idUser);

    @Query(value = "SELECT * FROM PROGRES_JASA WHERE ID_SPMK = ?1", nativeQuery = true)
    Iterable<Milestone> findByIdSPMK(Long idSPMK);

    @Transactional
    @Modifying
    @Query(value = "UPDATE PROGRES_JASA SET STATUS_RENCANA = ?2 WHERE ID_PROGRES = ?1", nativeQuery = true)
    void updateById(Long idProgres, String status);


}
