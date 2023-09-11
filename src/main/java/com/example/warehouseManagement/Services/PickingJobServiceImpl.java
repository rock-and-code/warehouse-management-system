package com.example.warehouseManagement.Services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.PickingJob;
import com.example.warehouseManagement.Domains.PickingJobLine;
import com.example.warehouseManagement.Repositories.PickingJobLineRepository;
import com.example.warehouseManagement.Repositories.PickingJobRepository;

@Service
public class PickingJobServiceImpl implements PickingJobService {
    private final PickingJobRepository pickingJobRepository;
    private final PickingJobLineRepository pickingJobLineRepository;

    

    public PickingJobServiceImpl(PickingJobRepository pickingJobRepository,
            PickingJobLineRepository pickingJobLineRepository) {
        this.pickingJobRepository = pickingJobRepository;
        this.pickingJobLineRepository = pickingJobLineRepository;
    }

    @Override
    public Iterable<PickingJob> findAll() {
        return pickingJobRepository.findAll();
    }

    @Override
    public Optional<PickingJob> findById(Long id) {
        return pickingJobRepository.findById(id);
    }

    @Override
    public PickingJob save(PickingJob pickingJob) {
        //return pickingJobRepository.save(pickingJob);
        PickingJob savedPickingJob = pickingJobRepository.save(pickingJob);
        for (PickingJobLine pickingJobLine : savedPickingJob.getPickingJobLines())
            pickingJobLine.setPickingJob(savedPickingJob);
        pickingJobLineRepository.saveAll(savedPickingJob.getPickingJobLines());
        return pickingJobRepository.save(savedPickingJob);
    }

    @Override
    public void delete(PickingJob pickingJob) {
        pickingJobRepository.delete(pickingJob);
    }
    
}
