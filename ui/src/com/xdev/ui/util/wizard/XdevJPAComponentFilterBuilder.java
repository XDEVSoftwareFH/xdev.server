
package com.xdev.ui.util.wizard;


import com.xdev.ui.entitycomponent.GenericEntityComponent;
import com.xdev.ui.util.masterdetail.JPAMasterDetail;


public class XdevJPAComponentFilterBuilder implements JPAComponentFilterBuilder
{
	private GenericEntityComponent<?, ?>	masterComponent, detailComponent;
	private Class<?>						masterEntity, detailEntity;
	
	private final JPAMasterDetail			masterDetail;
	
	
	public XdevJPAComponentFilterBuilder()
	{
		// TODO customizable?
		this.masterDetail = new JPAMasterDetail.Implementation();
	}
	
	
	@Override
	public void execute()
	{
		this.masterDetail.connectMasterDetail(this.masterComponent,this.detailComponent,
				this.masterEntity,this.detailEntity);
	}
	
	
	@Override
	public void setMasterComponent(final GenericEntityComponent<?, ?> masterComponent)
	{
		this.masterComponent = masterComponent;
	}
	
	
	@Override
	public void setDetailComponent(final GenericEntityComponent<?, ?> detailComponent)
	{
		this.detailComponent = detailComponent;
	}
	
	
	@Override
	public <T> void setMasterEntity(final Class<T> masterEntity)
	{
		this.masterEntity = masterEntity;
	}
	
	
	@Override
	public <T> void setDetailEntity(final Class<T> detailEntity)
	{
		this.detailEntity = detailEntity;
	}
	
}